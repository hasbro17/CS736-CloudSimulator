import java.io.BufferedWriter;
import java.util.Random;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter; 

public class lowVariability {
	public double generator() {
		//Checking if we need to update the mean yet
		if (startupDone == 1)
			{
				counter--;
				delta = deltaFraction*originalMean;
			}
		else {
			if (currentDemand < 0.90 * originalMean)
				startupDone = 1;
		}
		if(counter==0) 
		{
			//Generate a new mean between 70%-130% of the original mean and 10% of currentMean
			while (currentMean > (1+meanBound/100)*originalMean | currentMean < (1-meanBound/100)*originalMean) {
				currentMean = currentMean * ((100-meanBound) + randomGenerator.nextInt(2*meanBound))/100;
			}
//			System.out.println("New Mean: " + currentMean);
			counter = counterInit;
		}		
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
		this.currentMean = mean;
		this.delta = delta;  
		this.counter = this.counterInit;		
		this.currentDemand = 0;
		this.startupDone = 0;
	}
	public lowVariability(double mean) {
		randomGenerator = new Random();
		this.originalMean = mean;
		this.currentMean = mean;
		this.delta = initialDeltaFraction *mean;  
		this.counter = this.counterInit;
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
	private static final double deltaFraction = 0.001;
	private static final double initialDeltaFraction = 0.01;  
	private static final int distanceMultiplier = 50;
	private static final int counterInit = 1000;  
	private static final int meanBound = 30; // all means will be within 30% of original mean  
	public static void main(String[] args) throws IOException {
			File fout = new File("out.txt");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			double demand;
			double mean = 400;
			lowVariability gen1 = new lowVariability(mean);
//			System.out.println("Initial Demand: " + gen1.currentDemand);
		for(int i=0; i < 50000; i++) {
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
