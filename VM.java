import java.util.ArrayList;

//VM Class responsible for running a set of processes and reporting usage statistics
public class VM {

	//Static VMID
	private static int VMID=1;
	//unique VM ID
	private int vmID;
	//List of processes
	private ArrayList<Proc> procs;
	//Instance specs
	private int vCPU;
	private int RAM;//units: GBs
	//Time steps in minutes since VM running
	private int time;
	//Per hour cost
	private double hourlyRate;
	//Instance name
	private String instanceName;

	//constructor
	public VM(int vCPU, int RAM, double hourlyRate, String instanceName){
		this.vCPU=vCPU;
		this.RAM=RAM;
		this.hourlyRate=hourlyRate;
		this.instanceName=instanceName;
		procs=new ArrayList<Proc>();
		vmID=VMID;
		VMID++;
	}

	//Compute one time step for all procs
	//Returns cost increment for this step
	public double computeNextStep(){
		for (Proc proc : procs) {
			proc.computeNextStep();
		}
		time++;
		return hourlyRate/(60*1.0);
	}

	//Get instance name
	public String getInstanceName(){
		return instanceName;
	}

	//Get vmID
	public int getVMID(){
		return vmID;
	}

	//Get number of procs
	public int getNumProcs(){
		return procs.size();
	}

	//Add process to VM, add to sorted lists as well
	public void addProc(Proc proc){
		procs.add(proc);
	}
	
	public Proc removeProc(int pid){
		Proc toRemove=null;
		for(int i=0; i<procs.size(); i++)
		{
			if(procs.get(i).getPID()==pid){
				toRemove=procs.remove(i);
			}
		}
		return toRemove;
	}

	//Get Cost of VM since creation
	double getTotalCost(){
		return hourlyRate*(time*1.0/60);
	}

	//Memory usage as a fraction of total RAM.
	double getMemUtil(){
		double totalProcUsage=0;
		for (Proc proc : procs) {
			totalProcUsage+=proc.getMemUsage();
		}
		return (totalProcUsage)/(RAM*1024*1.0);
	}

	//CpuUsage as a fraction of total cores
	double getCPUUtil(){
		double totalProcUsage=0;
		for (Proc proc : procs) {
			totalProcUsage+=proc.getCPUUsage();
		}
		return (totalProcUsage)/(vCPU*1.0);
	}

	//The ordered lists allow policy to get min max and anything in between
	//Can change to getMax and getMin methods if thats all thats needed later
	
	//Get an array list of procs sorted(ascending) by their fractional cpu usage
	ArrayList<Proc> getCPUOrderedProcs(){
		ArrayList<Proc> cpuOrdered = new ArrayList<Proc>();
		//Insertion sort
		for (Proc proc : procs) {
			int i=0;
			while(i<cpuOrdered.size() && proc.getCPUUsage()>=cpuOrdered.get(i).getCPUUsage())
				i++;
			cpuOrdered.add(i, proc);
		}
		return cpuOrdered;
	}

	//Get an array list of procs sorted(ascending) by their memory usage
	ArrayList<Proc> getMemOrderedProcs(){
		ArrayList<Proc> memOrdered = new ArrayList<Proc>();
		for (Proc proc : procs) {
			int i=0;
			while(i<memOrdered.size() && proc.getMemUsage()>=memOrdered.get(i).getMemUsage())
				i++;
			memOrdered.add(i, proc);
		}
		return memOrdered;
	}

}
