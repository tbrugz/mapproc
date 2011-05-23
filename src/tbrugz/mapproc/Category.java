package tbrugz.mapproc;

import java.util.ArrayList;
import java.util.List;

public class Category {
	double startVal;
	double endVal;
	String description;
	String styleId;
	
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
			if(value>=c.startVal && value<c.endVal) { return c; }
			cLast = c;
		}
		return cLast;
	}
}
