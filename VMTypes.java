import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class VMTypes {
	private String type;
	private int vcpu;
	private double RAM;
	private double hourlyRate;

	//constructor
	public VMTypes(String type, String vcpu, String RAM, String hourlyRate) {
		this.type = type;
		this.vcpu = Integer.parseInt(vcpu);
		this.RAM = Double.parseDouble(RAM);
		this.hourlyRate = Double.parseDouble(hourlyRate);
	}

	public String getType(){
		return type;
	}

	public int getVCPU(){
		return vcpu;
	}

	public double getMemory(){
		return RAM;
	}

	public double getHourlyRate(){
		return hourlyRate;
	}

	//Check if VM can stay below (mem)threshold by addition of new proc
	//demand:demand of proc, upLimit: upper bound fraction of total
	public boolean isBelowMax(double demand, double upBound){
		return (demand)/(RAM*1024) < upBound;
	}

	//Static members
	private static ArrayList<VMTypes> VMDictionary = new ArrayList<VMTypes>();
	private static ArrayList<VMTypes> cpu = new ArrayList<VMTypes>();
	private static ArrayList<VMTypes> mem = new ArrayList<VMTypes>();
	private static ArrayList<VMTypes> rate = new ArrayList<VMTypes>();

	public static ArrayList<VMTypes> getMemOrdered(){
		return mem;
	}

	public static ArrayList<VMTypes> getCPUOrdered(){
		return cpu;
	}

	public static ArrayList<VMTypes> getCostOrdered(){
		return rate;
	}

	//Initialize VM instance types
	public static void initVMTypes(String filename){
		//init dictionary
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				if(split.length!=4)
				{
					System.out.println("VMTypes format incorrect");
					System.exit(0);
				}
				VMTypes vm = new VMTypes(split[0], split[1], split[2], split[3]);
				VMDictionary.add(vm);
			}
			br.close();
		}
		catch ( Exception e) {
			System.out.println(e.getMessage());
		}

		//init sorted lists via insertion sort
		for (VMTypes vmType : VMDictionary) {
			int i=0;
			while(i<cpu.size() && vmType.getVCPU()>=cpu.get(i).getVCPU())
				i++;
			cpu.add(i, vmType);
			i=0;
			while(i<mem.size() && vmType.getMemory()>=mem.get(i).getMemory())
				i++;
			mem.add(i, vmType);
			i=0;
			while(i<rate.size() && vmType.getHourlyRate()>=rate.get(i).getHourlyRate())
				i++;
			rate.add(i, vmType);
		}

	}



}
