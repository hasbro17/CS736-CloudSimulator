import java.util.Random;

//Generic Resource Class capable of 3 types of usage patterns
public class Resource {
	
	//Mean usage
	private double mean;
	//Current usage
	private double currentUsage;
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
	
	//Returns raw usage number of resource
	public double getUsage(){
		return currentUsage;
	}
	
}
