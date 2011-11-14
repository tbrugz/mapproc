package tbrugz.mapproc;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

public class IndexedSeriesMetadata {

	public enum ValueType {
		INTEGER, FLOAT, DATE;
	}

	public String objectLabel;
	public String valueLabel;
	public ValueType valueType;
	public String measureUnit;
	
	static NumberFormat intNumFormat = NumberFormat.getIntegerInstance();
	static NumberFormat floatNumFormat = NumberFormat.getNumberInstance();
	static DateFormat dateFormat = DateFormat.getDateInstance();
	
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
			case DATE:
				return dateFormat.format(new Date(Math.round(d)));
		}
		throw new RuntimeException("Unknown value type: "+valueType);
	} 

}
