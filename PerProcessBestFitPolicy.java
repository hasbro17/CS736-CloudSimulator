import java.util.ArrayList;

//One VM per process policy with threshold based best fit scaling
public class PerProcessBestFitPolicy implements Policy {

	
	private GlobalMonitor global;
	
	private double max, min;//min and max thresholds
	private ArrayList<VMTypes> memOrderedTypes;

	//Set max and min thresholds(fraction) of best fit
	public PerProcessBestFitPolicy(double max, double min) {
		this.max=max;
		this.min=min;
		//Set memory sorted list of VM types available
		memOrderedTypes=VMTypes.getMemOrdered();
	}

	//Set global object for this policy
	public void setGlobal(GlobalMonitor global){
		this.global=global;
	}

	//Policy to allocate new set of procs to availaible VMs
	//Allocate to smallest of VMtypes initially
	public void allocateProcs(ArrayList<Proc> newProcs) {
		VMTypes type = memOrderedTypes.get(0);
		for (Proc proc : newProcs) {
			VM newVM = global.createVM(type);
			global.addNewProc(proc, newVM.getVMID());
		}
	}

	//Check and adjust the global state
	//Check VMs over 75% and pick applications with usage>75%
	public void adjust() {


		//Scale UP
		//Get all VM's above thresholds
		ArrayList<VM> aboveMax = global.getAboveMax(max);
		bestFit(aboveMax);
		
		//Scale Down
		ArrayList<VM> belowMin = global.getBelowMin(min);
		bestFit(belowMin);

	}

	
	public void bestFit(ArrayList<VM> aboveMax){
		for (VM src : aboveMax) {
			//Choose process from this VM, in this case only one
			Proc toMigrate=src.getMemOrderedProcs().get(0);

			//Find dst VM for this process
			//In this case, create new one from types
			VM dst=getTargetVM(toMigrate);

			//If dst found then migrate proc to new VM and close down old one
			if(dst!=null)
			{
				//migrate
				global.migrateProc(toMigrate.getPID(), src.getVMID(), dst.getVMID());
				//close down old
				global.removeVM(src.getVMID());
			}

		}
	}


	//Returns a best fit targetVM for the process for which memUtil is under max(80%)
	//Returns null when no VM found big enough
	private VM getTargetVM(Proc toMigrate){
		VM newVM=null;
		//From smallest to largest check the best fitting VM that stays under 80% with this proc
		for (VMTypes type : memOrderedTypes) {
			//Can directly check from type without creating a new VM
			newVM=new VM(type.getVCPU(), type.getMemory(), type.getHourlyRate(), type.getType());
			if(newVM.isBelowMax(toMigrate.getMemUsage(), max))
				return newVM;
		}
		//No VM found big enough for this proc's usage
		return null;
	}

}
