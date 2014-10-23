package tbrugz.mapproc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexedSeries {
	static Log log = LogFactory.getLog(IndexedSeries.class);
	
	Map<String, Double> values = new HashMap<String, Double>();
	
	public IndexedSeriesMetadata metadata = new IndexedSeriesMetadata();
	//String objectLabel;
	//String valueLabel;
	
	public static final String DEFAULT_DELIMITER = ";";
	
	public void readFromStream(BufferedReader reader) throws IOException {
		readFromStream(reader, DEFAULT_DELIMITER);
	}

	public void readFromStream(BufferedReader reader, String delimiter) throws IOException {
		String header = reader.readLine();
		String[] headers = header.split(delimiter);
		metadata.objectLabel = normalize(headers[0]);
		String[] valueFields = headers[1].split(":");
		metadata.valueLabel = normalize(valueFields[0]);
		//TODO: valueType can be: float, integer (and maybe date)
		metadata.valueType = IndexedSeriesMetadata.ValueType.FLOAT;

		if(valueFields.length>1) {
			try {
				metadata.valueType = IndexedSeriesMetadata.ValueType.valueOf(valueFields[1]);
			}
			catch(IllegalArgumentException e) {
				log.warn("Unknown valueType ["+valueFields[1]+"], assuming "+metadata.valueType);
			}
			
			if(valueFields.length>2 && valueFields[2]!=null) {
				metadata.measureUnit = valueFields[2];
			}
		}
		log.info("IndexedSeries: objLabel: ["+metadata.objectLabel+"]; valueLabel: ["+metadata.valueLabel+"]; valueType: ["+metadata.valueType+"]; measureUnit: ["+metadata.measureUnit+"]");
		
		String line = reader.readLine();
		//TODO: non-double values... like Date or String
		while(line!=null) {
			//---
			String[] linevals = line.split(delimiter);
			String s = linevals[0];
			Double d = Double.parseDouble(linevals[1]);
			values.put(s, d);
			//---
			line = reader.readLine();
		}
	}
	
	public Set<String> getKeys() {
		return values.keySet();
	}
	
	public Collection<Double> getValues() {
		//double[] vals = new double[values.size()];
		//for(Double d: values.values()) {}
		return values.values();
	}
	
	public Double getValue(String s) {
		return values.get(s)!=null?values.get(s).doubleValue():null;
	}
	
	public int size() {
		return values.keySet().size();
	}
	
	String normalize(String s) {
		return s.replaceAll("['\"]", "");
	}
	
}
