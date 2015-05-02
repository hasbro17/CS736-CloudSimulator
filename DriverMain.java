import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//Tester class for cloud simulation
public class DriverMain {
	
	//To be changed by policies
	//Set Median Window size, can be changed at any given point to get fast or slow moving median
	public static int MEDIANWINDOW=1;

	public static void main(String[] args) {
		
		//init Logger
		Logger logger = Logger.getLogger("DriverLog");
		FileHandler fh;
		SimpleFormatter formatter = new SimpleFormatter();
		try {
			fh  = new FileHandler("./DriverLog.log");
			fh.setFormatter(formatter);
			logger.addHandler(fh);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//initialize ProcSpawn object and use that in while loop to generate new processes
		ProcSpawn procSpawn = new ProcSpawn("procSpawnFile.txt");
		
		//List of VM types ready for policy to use
		VMTypes.initVMTypes("vmTypes.txt");
		logger.info("initVMTypes done!");
		
		//Create policy object
		//Policy policy = new PerProcessBestFitPolicy(0.8, 0.2);
		Policy policy = new MultiProcessMemoryBestFitPolicy(0.8, 0.2);
		
		GlobalMonitor global = new GlobalMonitor();
		
		//Attach global object to policy
		policy.setGlobal(global);
		

		//Initialize the state of global
		//Start off with 1 smallest VM maybe?
		
		//Main simulation loop, runs until all processes reach end of their trace files
		while(!procSpawn.allFinished())
		{
			//Decrement and check next arrival time
			//Get new processes if available
			if(!procSpawn.isEmpty() && procSpawn.checkNextArrivalTime()==0){
				//Get new procs
				ArrayList<Proc> newProcs = procSpawn.spawnNextSet();
				//policy allocates newProcs to VMs
				policy.allocateProcs(newProcs);
			}
			
			//Execute one cycle of global monitor
			global.computeNextStep();
			logger.info(global.toString());
			
			//Policy to adjust global state
			policy.adjust();
			//logger.info(policy.toString());
			
		}
	
	}

}
