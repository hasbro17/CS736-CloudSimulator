import java.io.BufferedWriter;
import java.util.Random;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter; 

public class lowVariability {
	public double generator() {
		//Checking if we need to update the mean yet
		if (startupDone == 0 & currentDemand<1.05*currentMean & currentDemand>0.95*currentMean) {
			startupDone = 1;
			delta = mindeltaFraction*currentMean; 
		}		
		if (startupDone == 0) {
			counter = 5;	// we want to set a new goal soon after the usage comes close to initial mean
		}
		if(startupDone == 1 & currentDemand<1.05*currentMean & currentDemand>0.95*currentMean) {
					delta = mindeltaFraction*currentMean;
		}
		if(counter==0) 
		{
			//Generate a new mean between 70%-130% of the original mean 
			currentMean = originalMean * ((100-meanBound) + randomGenerator.nextInt(2*meanBound))/100;
			System.out.println("New Mean: " + currentMean);
			delta = maxdeltaFraction*originalMean;
			counter = counterInit;
		}
		counter--;
		int randomInt = randomGenerator.nextInt(100);
	    if(randomInt < 50 + distanceMultiplier*((currentMean-currentDemand)/currentMean)) 
	    	currentDemand +=delta;
	    else
	    	currentDemand -=delta;	    
		return currentDemand;
	};
	public lowVariability(double mean, double delta) {
		randomGenerator = new Random();
		this.originalMean = mean;
		this.currentMean = 	originalMean * ((100-meanBound) + randomGenerator.nextInt(2*meanBound))/100;
		this.delta = delta;  
		this.counter = 0;		
		this.currentDemand = 0;
		this.startupDone = 0;
	}
	public lowVariability(double mean) {
		randomGenerator = new Random();
		this.originalMean = mean;
		this.currentMean = 	originalMean * ((100-meanBound) + randomGenerator.nextInt(2*meanBound))/100;
		this.delta = initialDeltaFraction * mean;  
		this.counter = 0;
		this.currentDemand = 0;
		this.startupDone = 0;
	}
	private Random randomGenerator;
	private double delta;
	private double originalMean;
	private double currentMean;
	private double currentDemand;
	private int counter;
	private int startupDone;
	private static final double maxdeltaFraction = 0.01;
	private static final double mindeltaFraction = 0.005;
	private static final double initialDeltaFraction = 0.1;  
	private static final int distanceMultiplier = 800;
	private static final int counterInit = 40;  
	private static final int meanBound = 30; // all means will be within 40% of original mean  
	
	public static void main(String[] args) throws IOException {
			double mean = 800;
			File fout = new File("trace-slow-m"+mean+".txt");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			double demand;
			
			lowVariability gen1 = new lowVariability(mean);
//			System.out.println("Initial Demand: " + gen1.currentDemand);
		for(int i=0; i < 120; i++) {
			demand = gen1.generator();
//			if(demand>2*mean | demand<0)
//				System.out.println("ERROR!! Demand: " + demand + " iteration: " +i);
//			System.out.println(demand);
			bw.write(String.valueOf(demand));
			bw.newLine();
		}
		bw.close();
	}
	

}
