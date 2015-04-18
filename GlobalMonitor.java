import java.util.ArrayList;

//Global Monitor Class 
public class GlobalMonitor {

	//VM's or local monitor instances
	private ArrayList<VM> localMonitors;
	//Total cost accumulated from running VM's since beginning
	private int totalCost;

	//Constructor
	public GlobalMonitor(){
		localMonitors=new ArrayList<VM>();
		totalCost=0;
	}

	//Compute one time step for all VM instances and update totalCost
	public void computeNextStep(){
		for (VM vm : localMonitors) {
			totalCost+=vm.computeNextStep();
		}
	}
	
	/////Methods to observe Global State/////
	
	//Depends on what the policy needs.
	//More ordered lists? min max etc
	
	
	//Return cumulative cost
	public int getTotalCost() {
		return totalCost;
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

	//Migrate process pid, return false if something wrong
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
		
		return true;
	}

}
