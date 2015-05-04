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

	//User constraints
	public static double costPerDayLimit=10;//Dollars
	public static int migPerDayLimit=20;

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
		//Policy policy = new PerProcessBestFitPolicy(1, 0.2);
		Policy policy = new MultiProcessMemoryBestFitPolicy(1, 0.2);

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

		//Total OverCommit
		fout = new File("OverCommit.txt");
		fos = new FileOutputStream(fout);
		BufferedWriter bwOverCommit = new BufferedWriter(new OutputStreamWriter(fos));

		//Total OverCommit
		fout = new File("UnusedMem.txt");
		fos = new FileOutputStream(fout);
		BufferedWriter bwUnused = new BufferedWriter(new OutputStreamWriter(fos));
		
		//VM TimeLine
		fout = new File("VMUtils.txt");
		fos = new FileOutputStream(fout);
		BufferedWriter bwVMUtils = new BufferedWriter(new OutputStreamWriter(fos));
		






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



			//Log graph values
			//Cost
			bwCost.write(String.valueOf(global.getTotalCost()));
			bwCost.newLine();
			bwCost.flush();
			//Num Migrations
			bwMigrations.write(String.valueOf(global.getNumMigrations()));
			bwMigrations.newLine();
			bwMigrations.flush();
			MEDIANWINDOW=1;
			//Total Overcommit
			bwOverCommit.write(String.valueOf(global.getOverCommit(1)));
			bwOverCommit.newLine();
			bwOverCommit.flush();
			
			//Total Unused
			bwUnused.write(String.valueOf(global.getTotalUnused()));
			bwUnused.newLine();
			bwUnused.flush();
			
			//Timeline: VM Utilizations
			
			
			

			//logger.info(global.getTime()+"Mihir");


			//Reset Median Window


			//Policy to adjust global state
			policy.adjust();


		}

	}

}
