package tbrugz.mapproc;

import java.util.ArrayList;
import java.util.List;

public class Category {
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
