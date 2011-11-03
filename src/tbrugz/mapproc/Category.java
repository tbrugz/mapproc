package tbrugz.mapproc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Category {
	static Log log = LogFactory.getLog(Category.class);
	static final String COLOR_REGEX = "[a-zA-Z0-9]{8}";
	static final Pattern colorPattern = Pattern.compile(COLOR_REGEX);

	double startVal;
	double endVal;
	String description;
	String styleId;
	String styleColor;
	String styleXML;
	
	@Override
	public String toString() {
		return "["+description+";"+startVal+"-"+endVal+";"+styleId+"]";
	}
	
	static List<Category> getCategoriesFromLimits(List<Double> vals) {
		List<Category> list = new ArrayList<Category>();
		for(int i=0;i<vals.size()-1;i++) {
			Category cat = new Category();
			cat.startVal = vals.get(i);
			cat.endVal = vals.get(i+1);
			cat.description = "category #"+(i+1);
			cat.styleId = String.valueOf(i);
			list.add(cat);
		}
		return list;
	}

	static List<Category> getCategoriesFromCSVStream(BufferedReader reader, String delimiter) throws IOException {
		//id;min;max[;color]
		//---- id;min;max[;value-id[;color]]
		String header = reader.readLine();
		String[] headers = header.split(delimiter);
		//boolean hasValueId = false;
		boolean hasColor = false;
		switch(headers.length) {
			case 3: break;
			case 4: hasColor = true; break;
			//case 5: hasColor = true; break;
			default:
				log.warn("line: "+header);
				throw new RuntimeException("categories must have between 3 and 5 columns: #"+headers.length);
		}

		//log.info("IndexedSeries: objLabel: "+metadata.objectLabel+"; valueLabel: "+metadata.valueLabel+"; valueType: "+metadata.valueType);
		List<Category> list = new ArrayList<Category>();
		String line = reader.readLine();
		int i = 0;
		while(line!=null) {
			String[] linevals = line.split(delimiter);
			Category cat = new Category();
			//---
			cat.styleId = "category #"+(i+1)+": "+linevals[0];
			cat.description = linevals[0];
			cat.startVal = Double.parseDouble(linevals[1]);
			cat.endVal = Double.parseDouble(linevals[2]);

			log.debug("line:: ["+line+"] #"+linevals.length+"; "+(hasColor && linevals.length>=4?"hasColor!":""));
			
			if(hasColor && linevals.length>=4) {
				//log.debug("hasColor");
				if(colorPattern.matcher(linevals[3]).matches()) {
					cat.styleColor = linevals[3];
				}
				else {
					log.warn("not a valid color (0): "+linevals[3]);
				}
				//int hexVal = Integer.parseInt(linevals[3], 16);
			}
			//---
			list.add(cat);
			line = reader.readLine();
			i++;
		}
		return list;
	}
	
	static Category getCategoryFromValue(List<Category> cats, Double value) {
		Category cLast = null;
		for(Category c: cats) {
			if(value<c.endVal) { return c; }
			//if(value>=c.startVal && value<c.endVal) { return c; } //may not return correct category on lowest value
			cLast = c;
		}
		return cLast;
	}

	public double getStartVal() {
		return startVal;
	}

	public double getEndVal() {
		return endVal;
	}

	public String getDescription() {
		return description;
	}

	public String getStyleId() {
		return styleId;
	}

	public String getStyleColor() {
		return styleColor;
	}
	
}
