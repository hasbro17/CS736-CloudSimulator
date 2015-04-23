import java.util.ArrayList;

//Policy interface enforced on all kinds of policy types
public interface Policy {
	
	//Attach global to policy
	public void setGlobal(GlobalMonitor global);
	
	//Policy to allocate new set of procs to availaible VMs
	public void allocateProcs(ArrayList<Proc> newProcs);
	
	//Check and adjust the global state
	public void adjust();
	
}
