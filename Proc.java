
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
		public Proc(Pattern cpuType, Pattern memType){
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
}
