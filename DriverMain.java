import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

//Tester class for cloud simulation
public class DriverMain {

	private static ArrayList<VMTypes> VMDictionary = new ArrayList<VMTypes>();
	private static ArrayList<VMTypes> vcpu = new ArrayList<VMTypes>();
	private static ArrayList<VMTypes> mem = new ArrayList<VMTypes>();
	private static ArrayList<VMTypes> rate = new ArrayList<VMTypes>();
	private static Random randomGen = new Random();//random generator for workloads

	public static void initVMTypes(String filename){
		//init dictionary
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				VMTypes vm = new VMTypes(split[0], split[1], split[2], split[3]);
				VMDictionary.add(vm);
			}
		}
		catch ( Exception e) {
			System.out.println(e.getMessage());
		}
		
		//init sorted lists via insertion sort
		for (VMTypes vmType : VMDictionary) {
			int i=0;
			while(i<vcpu.size() && vmType.getVCPU()>=vcpu.get(i).getVCPU())
				i++;
			vcpu.add(i, vmType);
			i=0;
			while(i<mem.size() && vmType.getMemory()>=mem.get(i).getMemory())
				i++;
			mem.add(i, vmType);
			i=0;
			while(i<rate.size() && vmType.getHourlyRate()>=rate.get(i).getHourlyRate())
				i++;
			rate.add(i, vmType);
		}

	}
	
	

	public static void main(String[] args) {
		int minBuffer=30;
		int maxBuffer=100;
		int nextArrival=randomGen.nextInt(minBuffer)+maxBuffer;
		int maxProcesses=50;
		
		
		//List of VM types ready
		initVMTypes("vmTypes");
		
		//Initialize policy object
		
		GlobalMonitor global = new GlobalMonitor();
		
		//Initial conditions
		//Num of VMs
		//No processes spawned yet
		
		//Main simulation loop
		while(//simulation time)
		{
			//Execute one cycle of global monitor
			global.computeNextStep();
			
			//Policy to adjust VM's or procs
			
		}
		
		
	
	}

}
