import java.util.ArrayList;

//Global Monitor Class 
public class GlobalMonitor {

	//VM's or local monitor instances
	private ArrayList<VM> localMonitors;
	//Total Cost of having all VM's
	private int cumulativeCost;
	
	
	//Create new instance type
	public void createVM(Instance type){
		//specs decided on type
		// localMonitors.add(new VM(int vCPU, int RAM, double charge));
	}
	
	//remove only if no procs running?
	public void removeVM(int vmID){
		
	}
	
	//Process object or procID?
	public void migrateProc(Proc process, int srcID, int dstID){
		
	}
	
}
