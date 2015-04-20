
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
			cpu = new Resource(cpuType);
			mem = new Resource(memType);
		}
		
		//Update usage for process
		public void computeNextStep(){
			cpu.computeNextStep();
			mem.computeNextStep();
			time++;
		}
		
		//Get pid
		public int getPID(){
			return pid;
		}
		
		//get CPU usage for proc
		//Units:Normalized to number of vcpus
		public double getCPUUsage(){
			return cpu.getUsage()/(1000*1.0);
		}
		
		//get Mem usage for proc
		//Units: MBs for now
		public double getMemUsage(){
			return mem.getUsage();
		}
}
