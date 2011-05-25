package tbrugz.mapproc;

import java.text.NumberFormat;

public class IndexedSeriesMetadata {

	public enum ValueType {
		INTEGER, FLOAT, DATE;
	}

	public String objectLabel;
	public String valueLabel;
	public ValueType valueType;
	
	static NumberFormat intNumFormat = NumberFormat.getIntegerInstance();
	static NumberFormat floatNumFormat = NumberFormat.getNumberInstance();
	
	static {
		floatNumFormat.setGroupingUsed(true);
		floatNumFormat.setMinimumFractionDigits(2);
		floatNumFormat.setMaximumFractionDigits(2);
	}
	
	public String format(double d) {
		switch (valueType) {
			case INTEGER:
				return intNumFormat.format(d);
			case FLOAT:
				return floatNumFormat.format(d);
		}
		throw new RuntimeException("Unknown value type: "+valueType);
		//return null;
	} 

}
