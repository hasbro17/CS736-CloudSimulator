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
		DriverMain.MEDIANWINDOW=1;
		
		//Set memory sorted list of VM types available
		memOrderedTypes=VMTypes.getMemOrdered();
	}

	//Set global object for this policy
	public void setGlobal(GlobalMonitor global){
		this.global=global;
	}

	//Policy to allocate new set of procs to available VMs
	//Allocate to smallest of VMtypes initially
	public void allocateProcs(ArrayList<Proc> newProcs) {
		VMTypes type = memOrderedTypes.get(0);
		for (Proc proc : newProcs) {
			VM newVM = global.createVM(type);
			global.addNewProc(proc, newVM.getVMID());
		}
	}

	//Check and adjust the global state
	public void adjust() {

		//Scale UP
		//Get all VM's above thresholds, median window 1
		ArrayList<VM> aboveMax = global.getAboveMax(max);
		bestFit(aboveMax);
		
		//Scale Down, median window 1
		ArrayList<VM> belowMin = global.getBelowMin(min);
		bestFit(belowMin);

	}

	//Tries to find best fit new VMs for procs 
	public void bestFit(ArrayList<VM> outsideBounds){
		for (VM src : outsideBounds) {
			//Choose process from this VM, in this case only one
			Proc toMigrate=src.getMemOrderedProcs().get(0);
			
			//Don't migrate if above daily migration limit
			if(toMigrate.isAboveMigLimit())
				continue;

			//Find dst VM for this process
			//In this case, get the type of the new VM
			VMTypes dstType=getTargetVM(toMigrate,src);
			
			//If global cost for the day does not stay below limit with this addition
			if(dstType!=null && !global.staysBelowLimit(dstType.getHourlyRate(), src.getHourlyRate()) ){
				dstType=null;
			}

			//If dst found then migrate proc to new VM and close down old one
			if(dstType!=null)
			{
				//Create new VM in global
				VM dstVM=global.createVM(dstType);
				//migrate to new VM
				global.migrateProc(toMigrate.getPID(), src.getVMID(), dstVM.getVMID());
				//close down old VM(now empty)
				global.removeVM(src.getVMID());
			}

		}
	}


	//Returns a best fit targetVM for the process for which memUtil is under max(80%)
	//Returns null when no VM found big enough or if dst is same type as src
	private VMTypes getTargetVM(Proc toMigrate, VM src){
		//VM newVM=null;
		//From smallest to largest check the best fitting VM that stays under 80% with this proc
		for (VMTypes type : memOrderedTypes) {
			
			//Can directly check from type without creating a new VM
			if(type.isBelowMax(toMigrate.getMemUsage(), max))
			{
				//If best fit is the same as src then no need for new dst
				if(type.getType().equals(src.getInstanceName()))
					return null;
				else
					return type;
			}
			/*
			newVM=new VM(type.getVCPU(), type.getMemory(), type.getHourlyRate(), type.getType());
			if(newVM.isBelowMax(toMigrate.getMemUsage(), max))
				return type;
			*/
		}
		//No VM found big enough for this proc's usage
		return null;
	}

}
