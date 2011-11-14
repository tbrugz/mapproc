package tbrugz.mapproc.transform;

public class PolygonPlacemark {
	String id;
	String name;
	String description;
	String coordinates;

	final static String QUOT = "\"";
	
	//TODO: JSON escape
	String getJSON() {
		return "{"
				+" "+QUOT+"id"+QUOT+": "+QUOT+id+QUOT+","
				+" "+QUOT+"name"+QUOT+": "+QUOT+name+QUOT+","
				+" "+QUOT+"description"+QUOT+": "+QUOT+description+QUOT+","
				+" "+QUOT+"coordinates"+QUOT+": ["+coordinates+"]"
				+"}";
	}
	
}
