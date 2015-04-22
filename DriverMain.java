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

	//Initialize VM instance types
	public static void initVMTypes(String filename){
		//init dictionary
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				if(split.length!=4)
				{
					System.out.println("VMTypes format incorrect");
					System.exit(0);
				}
				VMTypes vm = new VMTypes(split[0], split[1], split[2], split[3]);
				VMDictionary.add(vm);
			}
			br.close();
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
		
		//initialize ProcSpawn object and use that in while loop to generate new processes
		ProcSpawn procSpawn = new ProcSpawn("procSpawnFile.txt");
		
		//List of VM types ready for policy to use
		initVMTypes("vmTypes.txt");
		
		//Create policy object
		//Policy policy = new Policy();
		
		GlobalMonitor global = new GlobalMonitor();
		
		//Initialize global state, using policy object
		//policy.init(global)
		//Might not need init, we can start off with 1 smallest vm
		
		//Main simulation loop, runs until all processes reach end of their trace files
		while(!procSpawn.allFinished())
		{
			//Decrement and check next arrival time
			//Get new processes if available
			if(procSpawn.checkNextArrivalTime()==0){
				//Get new procs
				ArrayList<Proc> newProcs = procSpawn.spawnNextSet();
				//policy allocates newProcs to VMs
				//policy.allocateProcs(global, newProcs)
			}
			
			//Execute one cycle of global monitor
			global.computeNextStep();
			
			//Policy to adjust global state
			//policy.adjust(global)
			
		}
	
	}

}
