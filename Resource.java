import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

//Generic Resource Class capable of 3 types of usage patterns
//Now a glorified file reader :/
public class Resource {

	//Resource types
	public static final String STATIC="static";
	public static final String FAST="fast";
	public static final String SLOW="slow";

	//Current usage
	private double currentUsage;

	//true when process trace finishes from file, signal for VM to remove it
	private boolean finished;

	//Scanner to read usage from file
	private Scanner scnr;

	//Resource type
	private String type;

	//History of usage
	ArrayList<Double> history = null;
	//Window size
	int maxWindowSize;

	//Constructor prepares scanner and reads fileName to tag resource type(static,slow,fast)
	public Resource(String fileName){
		File file = new File(fileName);
		try {
			scnr=new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not find resource file: "+fileName);
			e.printStackTrace();
		}
		//Filename must be of format: "trace-<type>-mNNN.txt"
		String tokens[] = fileName.split("-");
		//Static by default
		type=STATIC;
		if(tokens.length>2)
		{
			if(tokens[1].equals(FAST))
				type=FAST;
			else if(tokens[1].equals(SLOW))
				type=SLOW;
		}

		history=new ArrayList<Double>();
		maxWindowSize=1000;
	}

	//Update usage by reading in next usage from file
	//new line means finished
	public double computeNextStep(){
		if(scnr.hasNextLine()){
			currentUsage=scnr.nextDouble();
			scnr.nextLine();//clear line
			
			if(!scnr.hasNextLine())
				finished=true;
		}
		else{//trace finished
			finished=true;
			currentUsage=0;//process usage finishes, no load
		}

		//Build up history of window size for running median
		if(history.size()<maxWindowSize)
		{
			history.add(0,currentUsage);
		}
		else
		{
			history.remove(maxWindowSize-1);
			history.add(0, currentUsage);
		}


		return currentUsage;
	}


	//Get running median of mem utilization for a window size of k
	//Return fractional mem utilization from 
	public double getMedianUsage(){
		//Get window of history
		ArrayList<Double> window = new ArrayList<Double>();
		int i=0;
		int size=Math.min(history.size(),DriverMain.MEDIANWINDOW);
		while(i<size){
			window.add(history.get(i));
			i++;
		}
		Collections.sort(window);
		double medianUsage=0;
		if(window.size()>0)
			medianUsage=(window.get((size-1)/2)); 
		return medianUsage;
	}

	//Returns raw usage number of resource
	public double getUsage(){
		return currentUsage;
	}

	//To check if process finished
	public boolean isFinished(){
		return finished;
	}

	//Deprecated random generation
	/*	
	//Mean usage
	private double mean;
	//Type of usage pattern
	private Pattern type;
	//Generator types
	private HighVariability highGen;
	private lowVariability lowGen;
	private staticGenerator staticGen;
	//Random Generator for mean
	private Random randomGen= new Random();

	//Random mean resource usage between 1-1000
	public Resource(Pattern type){
		this.type=type;
		this.mean=randomGen.nextInt(1000)+1;
		highGen = new HighVariability(mean);
		lowGen = new lowVariability(mean);
		staticGen = new staticGenerator(mean);
	}

	//Set specified mean
	public Resource(Pattern type, int mean){
		this.type=type;
		this.mean=mean;
		highGen = new HighVariability(mean);
		lowGen = new lowVariability(mean);
		staticGen = new staticGenerator(mean);
	}


	//Compute, update and return the new currentUsage
	public double computeNextStep(){
		//Change in usage based on type
		if(type.equals(Pattern.Fast))
		{
			currentUsage=highGen.generator();
		}
		else if (type.equals(Pattern.Slow)) 
		{
			currentUsage=lowGen.generator();
		}
		else//Static
		{
			currentUsage=staticGen.generator();
		}
		return currentUsage;
	}


	 */
}
