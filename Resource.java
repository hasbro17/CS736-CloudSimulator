
//Generic Resource Class capable of 3 types of usage patterns
public class Resource {
	
	//Percent memory/cpu usage
	
	private double currentUsage;
	private Pattern type;
	
	//Possible fields depending on usage functions
	private double targetUsage;//to grow towards and become static at
	private double fastRate;//d/dt
	private double slowRate;
	private double jitter;//around static or level
	
	//Uninspiring Constructor
	public Resource(Pattern type){
		this.type=type;
	}
	
	//Compute, update and return the new currentUsage
	public double computeNextStep(){
		//Change in usage
		
		return currentUsage;
	}
	
	private void computeFast(){
		
	}
	
	private void computeSlow(){
		
	}
	
	
}
