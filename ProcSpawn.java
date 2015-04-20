import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


//Class to spawn new processes either randomly based on some parameters, or read a file to generate a previous trace of spawns
public class ProcSpawn {

	int nextArrival;//time till arrival of next process
	ArrayList<Proc> nextSet;//Next process spawned after delay done
	ArrayList<Proc> allProcs;//All procs read from file
	ArrayList<Integer> delays;//delay time for each process in sequence from previous
	Scanner fileIn=null;
	boolean done=false;//true when no more procs left
	int currIndex;//index of next process to spawn

	//Constructor for predetermined generation using file
	//File Format:Every line is a process with three attributes
	//<DELAY> <cpuTraceFile> <memTraceFile>
	//Delay is delay until this process is generated after the last one.
	//Consecutive processes with 0 delay are generated at together at the next call
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
		//Read all procs in
		readAllProcs();
		//Initialize first set of procs and delay
		genNextSet();
	}

	//Return current set of procs, setup next set of procs and their delay
	//Return null if there are no more procs to be spawned
	//Should only be called when checkTime returns 0
	public ArrayList<Proc> spawnNextSet(){
		//To return
		ArrayList<Proc> currentSet=nextSet;
		//refresh and generate nextSet and set delay
		genNextSet();
		//Generate Proc and return
		return currentSet;
	}

	//Decrement and check time until next process spawn
	//When zero you can call spawnNewProc
	public int checkTime(){
		if(nextArrival>0)
			nextArrival--;
		return nextArrival;
	}
	
	//To check if no more processes left
	public boolean isDone(){
		return done;
	}

	//Generate next set of processes
	private void genNextSet(){
		nextSet= new ArrayList<Proc>();
		if(!done)//still atleas one proc remaining
		{
			//add next process
			nextSet.add(allProcs.get(currIndex));
			nextArrival=delays.get(currIndex);
			currIndex++;
			//add remaining consecutive procs with 0 delay
			while(currIndex<allProcs.size() && delays.get(currIndex)==0)
			{
				nextSet.add(allProcs.get(currIndex));
				nextArrival=delays.get(currIndex);
				currIndex++;
			}
			//Set done to true if no more procs left
			if(currIndex==allProcs.size())
				done=true;
		}
	}

	//Read next line and set arrival and proc
	private void readAllProcs(){
		int delay=0;
		while(fileIn.hasNextLine())
		{
			String[] procAttrs=fileIn.nextLine().split("\\s+");//whitespace delimited
			if(procAttrs.length!=3){
				System.out.println("Proc file format issue");
			}
			else
			{
				try{
					delay=Integer.parseInt(procAttrs[0]);
					//Create next proc with usage trace files
					delays.add(nextArrival);
					allProcs.add(new Proc(procAttrs[1], procAttrs[2]));
				}catch(NumberFormatException e){
					System.out.println("Proc file format issue");
				}
			}

		}

	}
}
