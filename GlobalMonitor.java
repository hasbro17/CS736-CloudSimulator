import java.util.ArrayList;
import java.util.Collections;

//Global Monitor Class 
public class GlobalMonitor {

	//VM's or local monitor instances
	private ArrayList<VM> localMonitors;
	//Total cost accumulated from running VM's since beginning
	private int totalCost;
	private int numMigrations;

	//Constructor
	public GlobalMonitor(){
		localMonitors=new ArrayList<VM>();
		totalCost=0;
		numMigrations=0;
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
		VM vm = new VM(vmType.getVCPU(), vmType.getMemory(), vmType.getHourlyRate(), vmType.getType());
		localMonitors.add(vm);
	}

	
	
	
	
	
	//remove VM, return false if not found. 
	public boolean removeVM(int vmID){
		boolean removed=false;
		for(int i=0; i<localMonitors.size(); i++)
		{
			if(localMonitors.get(i).getVMID()==vmID){
				localMonitors.remove(i);
				removed=true;
			}
		}
		return removed;
	}

	//Migrate process pid, return false if something wrong(non existent process or VM)
	//Also associate migration delay/number of delays
	public boolean migrateProc(int pid, int srcID, int dstID){
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
		return true;
	}

}
