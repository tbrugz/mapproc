package tbrugz.mapproc.transform;

import java.util.ArrayList;
import java.util.List;

public class PolygonPlacemark {
	String id;
	String name;
	String description;
	List<String> coordinates = new ArrayList<String>(); //list of list of coordinates (for multi-geometry placemarks)

	final static String QUOT = "\"";
	
	//TODO: JSON escape
	String getJSON() {
		StringBuffer coords = new StringBuffer();
		int i=0;
		for(String s: coordinates) {
			if(i>0) { coords.append(", "); }
			coords.append("["+s+"]");
			i++;
		}
		return "{"
				+" "+QUOT+"id"+QUOT+": "+QUOT+id+QUOT+","
				+" "+QUOT+"name"+QUOT+": "+QUOT+name+QUOT+","
				+" "+QUOT+"description"+QUOT+": "+QUOT+description+QUOT+","
				+" "+QUOT+"coordinates"+QUOT+": ["+coords.toString()+"]"
				+"}";
	}
	
}
