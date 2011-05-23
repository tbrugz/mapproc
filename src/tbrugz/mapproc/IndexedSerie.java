package tbrugz.mapproc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IndexedSerie {
	Map<String, Double> values = new HashMap<String, Double>();
	
	public static final String DELIMITER = ";";
	
	public void readFromStream(BufferedReader reader) throws IOException {
		String header = reader.readLine();
		String[] headers = header.split(DELIMITER);
		String line = reader.readLine();
		//TODO: non-double values... like Date or String
		while(line!=null) {
			//---
			String[] linevals = line.split(DELIMITER);
			String s = linevals[0];
			Double d = Double.parseDouble(linevals[1]);
			values.put(s, d);
			//---
			line = reader.readLine();
		}
	}
	
	public Collection<Double> getValues() {
		//double[] vals = new double[values.size()];
		//for(Double d: values.values()) {}
		return values.values();
	}
	
	public Double getValue(String s) {
		return values.get(s)!=null?values.get(s).doubleValue():null;
	}
	
}
