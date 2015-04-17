import java.io.*;
import java.util.ArrayList;


public class CostModel {

	ArrayList<VMTypes> VMDictionary;

	//constructor
	public CostModel(String filename) {
		try {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			String[] split = line.split(",");
			VMTypes vm = new VMTypes(split[0], split[1], split[2], split[3]);
			VMDictionary.add(vm);
		}
		}
		catch ( Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
