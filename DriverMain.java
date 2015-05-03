import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//Tester class for cloud simulation
public class DriverMain {
	
	//To be changed by policies
	//Set Median Window size, can be changed at any given point to get fast or slow moving median
	public static int MEDIANWINDOW=1;

	public static void main(String[] args) throws IOException {
		
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
		Policy policy = new PerProcessBestFitPolicy(0.8, 0.2);
		
		GlobalMonitor global = new GlobalMonitor();
		
		//Attach global object to policy
		policy.setGlobal(global);
		
		
		//Init buffered writers for graph outputs
		//Cost: 
		//time Cost/day
		File fout = new File("Cost.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bwCost = new BufferedWriter(new OutputStreamWriter(fos));
		
		//Num Migrations: 
		//time NumMigrations
		fout = new File("NumMigrations.txt");
		fos = new FileOutputStream(fout);
		BufferedWriter bwMigrations = new BufferedWriter(new OutputStreamWriter(fos));
		
		//Average Overutilization
		fout = new File("OverUtil.txt");
		fos = new FileOutputStream(fout);
		BufferedWriter bwAVOverUtil = new BufferedWriter(new OutputStreamWriter(fos));
		
		
		
		
		

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
			
			
			
			//Log graph values
			//Cost
			bwCost.write(String.valueOf(global.getCostPerDay()));
			bwCost.newLine();
			//Num Migrations
			bwMigrations.write(global.getNumMigrations());
			bwMigrations.newLine();
			//AverageOverUtil
			global.getAboveMax(1);//Above 100% VMs
			
			
		}
	
	}

}
