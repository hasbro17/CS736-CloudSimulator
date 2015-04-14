import java.util.ArrayList;

//VM Class responsible for running a set of processes and reporting usage statistics
public class VM {
	
	//unique VM ID
	int vmID;
	//List of processes
	ArrayList<Proc> processes;
	//Instance specs
	int vCPU;
	int RAM;//units?
	//Time steps since VM running
	int time;
	//Per hour cost
	double charge;
	
	//constructor
	public VM(int vCPU, int RAM, double charge){
		this.vCPU=vCPU;
		this.RAM=RAM;
		this.charge=charge;
	}
	
	//Compute one time step for all procs
	public void computeNextStep(){
		for (Proc proc : processes) {
			proc.computeNextStep();
		}
	}
	
	//Add process to VM
	public void addProc(Proc proc){
		processes.add(proc);
	}
	
	//Get Cost of VM since creation
	double getTotalCost(){
		return charge*(time/60);
	}
	
	//Question:How does the vcpu and RAM numbers link with the percent utilization of the processes?
	//Resource usage is in percent.
	double getMemUtil(){
		return 0;
	}
	
	double getCPUUtil(){
		return 0;
	}
	
	
	
	

}
