import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	
	//To mark simulation end, all procs finished

	//Compute one time step for all procs
	//Returns cost increment for this step
	public double computeNextStep(){
		for (Proc proc : procs) {
			proc.computeNextStep();
		}
		time++;
		return hourlyRate/(60*1.0);
	}
	
	//Check if VM can stay below (mem)threshold by addition of new proc
	//demand:demand of proc, upLimit: upper bound fraction of total
	public boolean isBelowMax(double demand, double upBound){
		return (getRawMemUtil()+demand)/(RAM*1024) > upBound;
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

	//Remove a processes from this vm
	//Return null if not found
	public Proc removeProc(int pid){
		Proc toRemove=null;
		for(int i=0; i<procs.size(); i++)
		{
			if(procs.get(i).getPID()==pid){
				toRemove=procs.remove(i);
				break;
			}
		}
		return toRemove;
	}

	//Get Cost of VM since creation
	public double getTotalCost(){
		return hourlyRate*(time*1.0/60);
	}
	

	//Raw memory usage of all processes in VM
	public double getRawMemUtil(){
		double totalProcUsage=0;
		for (Proc proc : procs) {
			totalProcUsage+=proc.getMemUsage();
		}
		return totalProcUsage;
	}

	//Fractional memory usage of RAM in VM.
	public double getMemUtil(){
		return (getRawMemUtil())/(RAM*1024*1.0);
	}

	//Raw cpu usage of all processes in VM
	public double getRawCPUUtil(){
		double totalProcUsage=0;
		for (Proc proc : procs) {
			totalProcUsage+=proc.getCPUUsage();
		}
		return totalProcUsage;
	}

	//Fractional CpuUsage of all cores in VM
	public double getCPUUtil(){

		return (getRawCPUUtil())/(vCPU*1.0);
	}

	//The ordered lists allow policy to get min max and anything in between
	//Can change to getMax and getMin methods if thats all thats needed later

	//Get an array list of procs sorted(ascending) by their fractional cpu usage
	public ArrayList<Proc> getCPUOrderedProcs(){
		ArrayList<Proc> cpuOrdered = new ArrayList<Proc>(procs);
		Collections.sort(cpuOrdered, Proc.cpuCompare);
		return cpuOrdered;
	}
	
	//Get an array list of procs sorted(ascending) by their memory usage
	public ArrayList<Proc> getMemOrderedProcs(){
		ArrayList<Proc> memOrdered = new ArrayList<Proc>(procs);
		Collections.sort(memOrdered, Proc.memCompare);
		return memOrdered;
	}

	//VM Comparators used for VM sorting

	//Comparator for rawCPU order
	public static Comparator<VM> rawCPUCompare = new Comparator<VM>() {
		public int compare(VM v1, VM v2){
			if(v1.getRawCPUUtil()==v2.getRawCPUUtil())
				return 0;
			else
				return v1.getRawCPUUtil()>v2.getRawCPUUtil() ? 1:-1;
		}
	};

	//Comparator for rawMem order
	public static Comparator<VM> rawMemCompare = new Comparator<VM>() {
		public int compare(VM v1, VM v2){
			if(v1.getRawMemUtil()==v2.getRawMemUtil())
				return 0;
			else
				return v1.getRawMemUtil()>v2.getRawMemUtil() ? 1:-1;
		}
	};

	//Comparator for percent CPU utilization order
	public static Comparator<VM> cpuCompare = new Comparator<VM>() {
		public int compare(VM v1, VM v2){
			if(v1.getCPUUtil()==v2.getCPUUtil())
				return 0;
			else
				return v1.getCPUUtil()>v2.getCPUUtil() ? 1:-1;
		}
	};

	//Comparator for percent Mem utilization order
	public static Comparator<VM> memCompare = new Comparator<VM>() {
		public int compare(VM v1, VM v2){
			if(v1.getMemUtil()==v2.getMemUtil())
				return 0;
			else
				return v1.getMemUtil()>v2.getMemUtil() ? 1:-1;
		}
	};
	
	public String toString() {
		String result = "";
		result+="\t========================================\n";
		result+="\tVMID : " + this.vmID + " type : " + this.instanceName + " time : " + this.time + " MemUtil : "+this.getMemUtil() + "\n";
		//result.append("VMID : " + this.vmID + " type : " + this.instanceName + " time : " + this.time + "\n");
		for (Proc proc:procs) {
			result+="\t\tPID: " + proc.getPID() + " CPU usage: " + proc.getCPUUsage() + " Mem usage: " + proc.getMemUsage() + "\n";
		}
		result+="\t=========================================\n";
		return result.toString();
	}

}
