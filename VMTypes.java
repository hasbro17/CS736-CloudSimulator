public class VMTypes {
	private String type;
	private int vcpu;
	private int memory;
	private double hourlyRate;


	//constructor
	public VMTypes(String type, String vcpu, String memory, String hourlyRate) {
		this.type = type;
		this.vcpu = Integer.parseInt(vcpu);
		this.memory = Integer.parseInt(memory);
		this.hourlyRate = Double.parseDouble(hourlyRate);
	}
	
	public String getType(){
		return type;
	}
	
	public int getVCPU(){
		return vcpu;
	}
	
	public int getMemory(){
		return memory;
	}
	
	public double getHourlyRate(){
		return hourlyRate;
	}
	
}
