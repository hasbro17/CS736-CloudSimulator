import java.util.Comparator;


//Process class
public class Proc {

	// Static PID
	private static int PID = 1;
	//Pid
	private int pid;
	//Time steps in minutes since process running
	private int time;
	//Resource objects for memory and cpu
	private Resource cpu;
	private Resource mem;

	//Constructor
	public Proc(String cpuTypeFile, String memTypeFile){
		this.pid=PID;
		PID++;
		time=0;
		cpu = new Resource(cpuTypeFile);
		mem = new Resource(memTypeFile);
	}

	//Update usage for process
	public void computeNextStep(){
		cpu.computeNextStep();
		mem.computeNextStep();
		time++;
	}
	
	//check if process finished, both mem and cpu traces
	public boolean isFinished(){
		return cpu.isFinished() && mem.isFinished();
	}

	//Get pid
	public int getPID(){
		return pid;
	}

	//get CPU usage for proc MEDIAN
	//Units:Normalized to number of vcpus
	public double getCPUUsage(){
		return cpu.getMedianUsage()/(1000*1.0);
	}

	//get Mem usage for proc MEDIAN
	//Units: MBs for now
	public double getMemUsage(){
		return mem.getMedianUsage();
	}

	//Comparators used for sorting proc lists

	//Comparator for cpu order
	public static Comparator<Proc> cpuCompare = new Comparator<Proc>() {
		public int compare(Proc p1, Proc p2){
			if(p1.getCPUUsage()==p2.getCPUUsage())
				return 0;
			else
				return p1.getCPUUsage()>p2.getCPUUsage() ? 1:-1;
		}
	};

	//Comparator for mem order
	public static Comparator<Proc> memCompare = new Comparator<Proc>() {
		public int compare(Proc p1, Proc p2){
			if(p1.getMemUsage()==p2.getMemUsage())
				return 0;
			else
				return p1.getMemUsage()>p2.getMemUsage() ? 1:-1;
		}
	};


}
