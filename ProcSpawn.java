import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;


//Class to spawn new processes either randomly based on some parameters, or read a file to generate a previous trace of spawns
public class ProcSpawn {

	int minBuffer=30;
	int maxBuffer=100;
	int numProcs=50;
	Random randomGen=new Random();
	Pattern[] types=Pattern.values();
	
	boolean typeRandom;//true random and false file
	
	int nextArrival;//time till arrival of next process
	String outputFile="ProcGenLog";//output file
	
	Scanner fileIn=null;
	Scanner fileOut=null;
	
	
	//Constructor for randomized generation
	public ProcSpawn(int minBuffer, int maxBuffer, int numProcs){
		this.minBuffer=minBuffer;
		this.maxBuffer=maxBuffer;
		this.numProcs=numProcs;
		typeRandom=true;
		
		File file = new File(outputFile);
		try {
			fileOut=new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not open Process Log File: "+outputFile);
			e.printStackTrace();
		}
		
	}
	
	//Constructor for predetermined generation using file
	public ProcSpawn(String fileName){
		//open file and read all the procs in
		File file = new File(fileName);
		try {
			fileIn= new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not open Process File: "+fileName);
			e.printStackTrace();
		}
	}
	
	//Spawn new proc and return proc
	//Return null if there are no more procs to be spawned
	public Proc spawnNewProc(){
		Proc newProc = null;
		//Get new Proc parameters
		if(typeRandom)
		{
			//If random generation
			nextArrival=randomGen.nextInt(minBuffer)+maxBuffer;
			//Process mem and cpu type randomly chosen
			newProc = new Proc(types[randomGen.nextInt(types.length)], types[randomGen.nextInt(types.length)]);
		}
		else
		{
			if(fileIn.hasNextLine())
			{
				String[]procAttrs=fileIn.nextLine().split("\\s+");
				if(procAttrs.length==)
			}
		}
		
		//Generate Proc and return
		return newProc;
		
	}
		
	//Decrement and check time until next process spawn
	//When zero you can call spawnNewProc
	public int checkTime(){
		return 0;
	}
	
	//Logs new process creation, time and type to a file which can be read next time
	public void logToFile(){
		
	}
}
