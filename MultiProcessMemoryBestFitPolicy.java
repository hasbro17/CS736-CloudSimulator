/**********************************************************
 * 
 * Memory best fit policy when a VM can run more than one processes
 * 
 * Fixed cap on number of migrations per process per day and total cost per day
 * Each VM can have more than one processes. 
 * Policy has two parameters - max and min memory thresholds for VMs
 * 
 * 1. Find a VM whose mem utilization goes above max threshold.
 * 2. Pick the processes from this VM until memory utilization comes below max threshold. Only pick the processes that do not violate number of migrations constraint.
 * 3. Start from largest process and try to best fit it into existing VMs.
 * 4. If processes are leftover, decide the best combination of new VMs to be spawned to minimize cost. Consider the total cost per day here.
 * 5. Now best fit the processes into new VMs.
 * 
 * SideNote:
 * Cost/day restricts the number of VMs allowed. As number of VMs allowed decreases memory pressure increases. This can be shown as performance degradation (related to swaps, pgflts)
 * 
 ************************************************************/

import java.util.ArrayList;
public class MultiProcessMemoryBestFitPolicy implements Policy {

private GlobalMonitor global;
	
	private double max, min;//min and max thresholds
	private ArrayList<VMTypes> memOrderedTypes;

	//Set max and min thresholds(fraction) of best fit
	public MultiProcessMemoryBestFitPolicy(double max, double min) {
		this.max=max;
		this.min=min;
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
		//Get all VM's above thresholds
		ArrayList<VM> aboveMax = global.getAboveMax(max);
		bestFit(aboveMax);
		
		//Scale Down
		ArrayList<VM> belowMin = global.getBelowMin(min);
		bestFit(belowMin);

	}

	//Tries to find best fit new VMs for procs 
	public void bestFit(ArrayList<VM> outsideBounds){
		for (VM src : outsideBounds) {
			//Choose process from this VM, in this case only one
			Proc toMigrate=src.getMemOrderedProcs().get(0);

			//Find dst VM for this process
			//In this case, get the type of the new VM
			VMTypes dstType=getTargetVM(toMigrate,src);

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


