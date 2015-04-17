public class VMTypes {
	private String type;
	private int vcpu;
	private int memory;
	private int hourlyRate;


	//constructor
	public VMTypes(String type, String vcpu, String memory, String hourlyRate) {
		this.type = type;
		this.vcpu = Integer.parseInt(vcpu);
		this.memory = Integer.parseInt(memory);
		this.hourlyRate = Integer.parseInt(hourlyRate);
	}
}
