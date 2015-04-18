import java.io.BufferedWriter;
import java.util.Random;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter; 

public class staticGenerator {
	public double generator() {
		if (startupDone == 0 & currentDemand > 0.95 * mean) {
			startupDone = 1;
			delta = deltaFraction*mean;
		}
		Random randomGenerator = new Random();
	    int randomInt = randomGenerator.nextInt(100);
	    if(randomInt < 50 + distanceMultiplier*((mean-currentDemand)/mean)) 
	    	currentDemand +=delta;
	    else
	    	currentDemand -=delta;	    
		return currentDemand;
	};
	public staticGenerator(double mean, double delta) {
		this.mean = mean;
		this.delta = delta;  
		currentDemand = 0;
		startupDone = 0;
	}
	public staticGenerator(double mean) {
		this.mean = mean;
		this.delta = initdeltaFraction * mean;  
		currentDemand = 0;
		startupDone = 0;
	}
	private double delta;
	private double mean;
	private double currentDemand;
	private int startupDone;
	private static final double deltaFraction = 0.01;
	private static final double initdeltaFraction = 0.1; 
	private static final int distanceMultiplier = 500;  
	/*
	public static void main(String[] args) throws IOException{
			File fout = new File("out_static.txt");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			double demand;
			staticGenerator gen1 = new staticGenerator(400);
//			System.out.println("Initial Demand: " + gen1.currentDemand);
		for(int i=0; i < 120; i++) {
			demand = gen1.generator();
//			if(demand>1 | demand<0)
//				System.out.println("ERROR!! Demand: " + demand + " iteration: " +i);
//			System.out.println(demand);
			bw.write(String.valueOf(demand));
			bw.newLine();
		}
		bw.close();
	}
	*/

}
