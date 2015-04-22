import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//Global Monitor Class 
public class GlobalMonitor {

	//VM's or local monitor instances
	private ArrayList<VM> localMonitors;
	//Total cost accumulated from running VM's since beginning
	private int totalCost;
	private int numMigrations;
	
	private Logger logger = Logger.getLogger("GlobalMonitorLog");
	FileHandler fh;
	SimpleFormatter formatter = new SimpleFormatter();
	
	private void initLogger() {
		try {
			fh  = new FileHandler("./GlobalMonitorLog.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fh.setFormatter(formatter);
		logger.addHandler(fh);
	}

	//Constructor
	public GlobalMonitor(){
		localMonitors=new ArrayList<VM>();
		totalCost=0;
		numMigrations=0;
		initLogger();
	}

	//Compute one time step for all VM instances and update totalCost
	public void computeNextStep(){
		for (VM vm : localMonitors) {
			totalCost+=vm.computeNextStep();
		}
	}

	/////Methods to observe Global State/////

	//Return cumulative cost
	public int getTotalCost() {
		return totalCost;
	}	
	
	//Get number of migrations
	public int getNumMigrations(){
		return numMigrations;
	}

	//VMs ordered by rawCPUUsage
	public ArrayList<VM> getRawCPUOrder(){
		ArrayList<VM> rawCPUOrdered = new ArrayList<VM>(localMonitors);
		Collections.sort(rawCPUOrdered,VM.rawCPUCompare);
		return rawCPUOrdered;
	}

	//VMs ordered by rawMemUsage
	public ArrayList<VM> getRawMemOrder(){
		ArrayList<VM> rawMemOrdered = new ArrayList<VM>(localMonitors);
		Collections.sort(rawMemOrdered,VM.rawMemCompare);
		return rawMemOrdered;
	}

	//VMs ordered by percent cpuUsage
	public ArrayList<VM> getCPUOrder(){
		ArrayList<VM> cpuOrdered = new ArrayList<VM>(localMonitors);
		Collections.sort(cpuOrdered,VM.cpuCompare);
		return cpuOrdered;
	}

	//VMs ordered by percent memUsage
	public ArrayList<VM> getMemOrder(){
		ArrayList<VM> memOrdered = new ArrayList<VM>(localMonitors);
		Collections.sort(memOrdered,VM.memCompare);
		return memOrdered;
	}


	//////Methods to change Global State/////

	//Create new VM instance
	public void createVM(VMTypes vmType){
		logger.info("Adding VM => new state -");
		VM vm = new VM(vmType.getVCPU(), vmType.getMemory(), vmType.getHourlyRate(), vmType.getType());
		localMonitors.add(vm);
		logger.info(this.toString());
	}
	
	//remove VM, return false if not found. 
	public boolean removeVM(int vmID){
		logger.info("Removing VM => new state -");
		boolean removed=false;
		for(int i=0; i<localMonitors.size(); i++)
		{
			if(localMonitors.get(i).getVMID()==vmID){
				localMonitors.remove(i);
				removed=true;
			}
		}
		logger.info(this.toString());
		return removed;
	}
	
	//Add a new incoming process to a VM
	public boolean addNewProc(Proc proc, int dstID){
		VM dst=null;
		for (VM local : localMonitors) {
			if(local.getVMID()==dstID){
				dst=local;
				break;
			}
		}
		if(dst==null)
			return false;
		dst.addProc(proc);
		return true;
	}

	//Migrate process pid, return false if something wrong(non existent process or VM)
	//Also associate migration delay/number of delays
	public boolean migrateProc(int pid, int srcID, int dstID){
		logger.info("Migrating process => new state -");
		VM src=null;
		VM dst=null;
		for(int i=0; i<localMonitors.size(); i++)
		{
			if(localMonitors.get(i).getVMID()==srcID)
				src=localMonitors.get(i);

			if(localMonitors.get(i).getVMID()==dstID)
				dst=localMonitors.get(i);
		}

		if(src==null || dst==null)
			return false;
		Proc toMigrate=src.removeProc(pid);
		if(toMigrate==null)
			return false;
		dst.addProc(toMigrate);
		
		//Increment migrations counter
		numMigrations++;
		logger.info(this.toString());
		return true;
	}
	
	public String toString() {
		String result = "";
		result+="=================================================\n";
		result+="Global State at" + " time : " /*+ this.time*/ + " total cost : " + totalCost + " migrations : " + numMigrations + "\n";
		for (VM vm:localMonitors) {
			result+="\tVMID : " + vm.getVMID() + " type : " + vm.getInstanceName() + " num procs : " + vm.getNumProcs() + " cost: " + vm.getTotalCost() + "\n";
		}
		result+="=================================================\n";
		return result.toString();
	}

}
