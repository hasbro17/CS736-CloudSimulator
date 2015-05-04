import java.io.BufferedWriter;
import java.util.Random;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter; 

public class HighVariability {
	public double generator() {
		//Checking if we need to update the mean yet	
		if(currentDemand<1.05*currentMean & currentDemand>0.95*currentMean) {
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
	public HighVariability(double mean, double delta) {
		randomGenerator = new Random();
		this.originalMean = mean;
		this.currentMean = mean;
		this.delta = delta;  
		this.counter = 0;		
		this.currentDemand = 0;
//		this.startupDone = 0;
	}
	public HighVariability(double mean) {
		randomGenerator = new Random();
		this.originalMean = mean;
		this.currentMean = mean;
		this.delta = initialDeltaFraction * mean;  
		this.counter = 0;
		this.currentDemand = 0;
//		this.startupDone = 0;
	}
	private Random randomGenerator;
	private double delta;
	private double originalMean;
	private double currentMean;
	private double currentDemand;
	private int counter;
//	private int startupDone;
	private static final double maxdeltaFraction = 0.1;
	private static final double mindeltaFraction = 0.005;
	private static final double initialDeltaFraction = 0.1;  
	private static final int distanceMultiplier = 800;
	private static final int counterInit = 15;  
	private static final int meanBound = 50; // all means will be within 40% of original mean 
	
	
	public static void main(String[] args) throws IOException {
			int mean = 1600;
			File fout = new File("trace-fast-m"+mean+"-mb"+meanBound+".txt");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			double demand;
			HighVariability gen1 = new HighVariability(mean);
//			System.out.println("Initial Demand: " + gen1.currentDemand);
		for(int i=0; i <(3*24*60) ; i++) {
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
