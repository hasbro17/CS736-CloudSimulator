
//Process class
public class Proc {

		//Pid
		private int pid;
		//Time steps since process running
		private int time;
		//Resource objects for memory and cpu
		private Resource cpu;
		private Resource mem;
		
		//Constructor
		public Proc(int pid, Pattern cpuType, Pattern memType){
			this.pid=pid;
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
