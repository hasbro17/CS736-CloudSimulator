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
 * 4. (Knapsack problem with duplicates allowed)If processes are leftover, decide the best combination of new VMs to be spawned to minimize cost. Consider the total cost per day here.
 * 5. Now best fit the processes into new VMs.
 * 
 * FIXME:
 * Scale up and scale down need different handling
 * 1. Scale Down:
 * 		//Scale down should have a different approach than best fitting
		//procs into existing VMs.
		//1. Mark all procs in the belowMin VMs (so we effectively consider the belowMin VMs as empty now)
		//2. Try to pack these marked procs(largest/smallest first policy choice) into other VMs that were not underutilized.
		//3. Left over procs that could not be fitted in the second step are now tried against the empty VMs.
		//	 Pack them in the empty VMs.
		//4.Close down any empty VMs after this.
		 * 
	2. Scale Up:
		//In scale up while finding targetVM we should not consider all VMs as targets. Exclude the ones that
		 * were overUtil. Currently considers all.
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
		//DriverMain.MEDIANWINDOW=?
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
			// Find a VM from existing to allocate a new process
			VM vm = getFirstUnderutilizedExistingVM(proc);
			if (vm == null) {
				// Didn't find any existing VM so create new
				VM newVM = global.createVM(type);
				global.addNewProc(proc, newVM.getVMID());
			}
			else
				global.addNewProc(proc, vm.getVMID());
		}
	}

	//Check and adjust the global state
	public void adjust() {

		//Scale UP
		//Get all VM's above thresholds, median window 1
		ArrayList<VM> aboveMax = global.getAboveMax(max);
		ArrayList<Proc> leftoverProcs = bestFit(aboveMax);
		if (leftoverProcs != null) {
			// TODO: Call method to create new VMs and assign procs to them
			handleLeftoverProcesses(leftoverProcs);
		}
		


		//TODO Verify scale down
		//Scale Down, median window,1
		ArrayList<VM> belowMin = global.getBelowMin(min);
		bestFit(belowMin);

	}
	
	// Wrapper around solveKnapsack. Calls solveKnapsack and the best fits processes into the returned set
	private void handleLeftoverProcesses(ArrayList<Proc> leftovers) {
		ArrayList<VMTypes> targetTypes = solveKnapsack(leftovers);
		
		// now best fit the leftovers in these
		for (VMTypes vmTypes : targetTypes) {
			global.createVM(vmTypes);
		}
		allocateProcs(leftovers);
	}
	
	// Find the cheapest combination of new VMs (Knapsack problem)
	// W = total mem requirement of all leftover processes
	// wt[] = mem capacity of VM types
	// val[] = cost of VM types
	private ArrayList<VMTypes> solveKnapsack(ArrayList<Proc> leftovers) {
		ArrayList<VMTypes> memOrdered = VMTypes.getMemOrdered();
		ArrayList<Integer> wt = new ArrayList<Integer>();
		ArrayList<Double> val = new ArrayList<Double>();
		ArrayList<VMTypes> retVMTypes = new ArrayList<VMTypes>();
		int W = 0;
		for (VMTypes vmTypes : memOrdered) {
			int a=(int)vmTypes.getMemory();
			wt.add(a*1024);
			val.add(vmTypes.getHourlyRate());
		}
		for (Proc proc : leftovers) {
			W += proc.getMemUsage();
		}
		
		int N = wt.size();
		double[][] V = new double[N + 1][W + 1];
		for (int col = 0; col <= W; col++) {
			V[0][col] = 0;
		}
		for (int row = 0; row <= N; row++) {
			V[row][0] = 0;
		}
		for (int item = 1; item <= N; item++) {
			for (int weight = 1; weight <= W; weight++) {
				if (wt.get(item - 1) <= weight) {
					V[item][weight] = Math.max(val.get(item - 1)+ V[item - 1][weight - wt.get(item - 1)],V[item - 1][weight]);
					if (val.get(item - 1)+ V[item - 1][weight - wt.get(item - 1)] >= V[item - 1][weight])
						retVMTypes.add(memOrdered.get(item-1));
				}
				else {
					V[item][weight] = V[item - 1][weight];
				}
			}
		}
		return retVMTypes;
	}

	//Tries to find best fit VMs for procs 
	public ArrayList<Proc> bestFit(ArrayList<VM> outsideBounds){
		ArrayList<Proc> leftoverProcs = new ArrayList<Proc>();
		//Proc previousProc = null;
		int i=0;
		for (VM src : outsideBounds) {
			// TODO Choose largest/smallest processes from VM until the threshold is satisfied based on a flag
			//Proc toMigrate=src.getMemOrderedProcs().get(0);
			while (src.getMemUtil() > max) {
				Proc toMigrate=src.getMemOrderedProcs().get(i);
	
				//Find dst VM from existing VMs for this process
				VM dstVM=getExistingTargetVM(toMigrate);
				
				if (dstVM == null) {
					leftoverProcs.add(toMigrate);
					//previousProc = toMigrate;
				}
				i++;
			}
		}
		return leftoverProcs;
	}


	private VM getExistingTargetVM(Proc toMigrate) {
		double newMemUsage = 0;
		double memLeft = Double.MAX_VALUE;
		VM targetVM = null;
		for (VM vm : global.getLocalMonitors()) {
			newMemUsage = vm.getMemUtil() + toMigrate.getMemUsage();
			if (newMemUsage < max && memLeft < vm.getRAM() - newMemUsage) {
				targetVM = vm;
				memLeft = vm.getRAM() - newMemUsage;
			}
		}
		return targetVM;
	}

	
	// Find the first VM in the list that has some capacity.
	// Used to allocate new incoming processes
	private VM getFirstUnderutilizedExistingVM(Proc p) {
		ArrayList<VM> vms = global.getLocalMonitors();
		for (VM vm : vms) {
			if (vm.getMemUtil() < max)
				return vm;
		}
		// no VM exists with less than max utilization
		return null;
	}
}


