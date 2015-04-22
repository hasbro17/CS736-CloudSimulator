import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//Generic Resource Class capable of 3 types of usage patterns
//Now a glorified file reader :/
public class Resource {

	//Current usage
	private double currentUsage;
	private Scanner scnr;
	//true when process trace finishes from file, signal for VM to remove it
	private boolean finished;

	public Resource(String fileName){
		File file = new File(fileName);
		try {
			scnr=new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not find resource file: "+fileName);
			e.printStackTrace();
		}
	}

	//Update usage by reading in next usage from file
	//new line means finished
	public double computeNextStep(){
		if(scnr.hasNextLine()){
			currentUsage=scnr.nextDouble();
			scnr.nextLine();//clear line
		}
		else{//trace finished
			finished=true;
			currentUsage=0;//process usage finishes, no load
		}
		return currentUsage;
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
