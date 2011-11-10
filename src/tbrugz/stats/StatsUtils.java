package tbrugz.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tbrugz.mapproc.IndexedSeries;

public class StatsUtils {

	static Log log = LogFactory.getLog(StatsUtils.class);
	
	static final double[] ZERO_SIZE_DOUBLE_ARRAY = new double[0]; 
	
	//TODOne: escala por percentils... (necessário ordenar dados)
	
	public enum ScaleType {
		LINEAR, LOG, PERCENTILE; //, POLYNOMIAL;
	}
	
	public static double max(double[] vals) {
		double max = -Double.MAX_VALUE;
		for(double d: vals) {
			if(d>max) max=d;
		}
		return max;
	}

	public static double min(double[] vals) {
		double min = Double.MAX_VALUE;
		for(double d: vals) {
			if(d<min) min=d;
		}
		return min;
	}
	
	public static List<Double> getCategoriesLimits(ScaleType type, List<Double> vals, int numCategories) {
		if(type==ScaleType.PERCENTILE) {
			return getPercentileCategoriesLimits(vals, numCategories);
		}
		double[] valsArr = toDoubleArray(vals);
		double min = min(valsArr);
		double max = max(valsArr);

		log.info("min = "+min+"; max = "+max);
		
		switch (type) {
			case LINEAR:
				return getLinearCategoriesLimits(min, max, numCategories);
			case LOG: 
				return getLogCategoriesLimits(min, max, numCategories);
		}
		
		log.warn("ScaleType '"+type+"' not known");
		throw new RuntimeException("ScaleType '"+type+"' not known");
	}

	public static Collection<Double> getValsForExistingPlacemarks(IndexedSeries is, Document doc) {
		Set<String> keys = is.getKeys();
		List<Double> existingVals = new ArrayList<Double>();
		
		NodeList nList = doc.getElementsByTagName("Placemark");
		for (int i = nList.getLength()-1; i >= 0; i--) {
			Element eElement = (Element) nList.item(i);
			String id = eElement.getAttribute("id");
			if(keys.contains(id)) {
				existingVals.add(is.getValue(id));
			}
		}
		
		log.info("# of existing values: "+existingVals.size());
		return existingVals;
	}
	
	public static List<Double> getLinearCategoriesLimits(double min, double max, int numCategories) {
		List<Double> ld = new ArrayList<Double>();
		double amplitude = max-min;
		double interval = amplitude/numCategories;
		//ld.add(min);
		for(int i = 0;i<=numCategories;i++) {
			ld.add(min+interval*i);
		}
		return ld; //.toArray(new Double[]{});
	}

	public static List<Double> getLogCategoriesLimits(double min, double max, int numCategories) {
		double negativeDiff = 0;
		if(min<1) {
			negativeDiff = min-1;
		}
		double newMin = Math.log(min-negativeDiff);
		double newMax = Math.log(max-negativeDiff);
		List<Double> ld = new ArrayList<Double>();
		//System.out.println("zzz: "+newMin+", "+newMax);
		
		double amplitude = newMax-newMin;
		double interval = amplitude/numCategories;
		//ld.add(min);
		for(int i = 0;i<=numCategories;i++) {
			ld.add(Math.exp(newMin+interval*i)+negativeDiff);
		}
		return ld; //.toArray(new Double[]{});
	}
	
	public static List<Double> getPercentileCategoriesLimits(List<Double> vals, int numCategories) {
		List<Double> newvals = new ArrayList<Double>();
		newvals.addAll(vals);
		Collections.sort(newvals);
		int elemPerCategory = vals.size()/numCategories;
		List<Double> ld = new ArrayList<Double>();

		int count = 0;
		for(Double d: newvals) {
			if(count%elemPerCategory == 0) {
				ld.add(d);
				//log.info("limit: "+d);
			}
			else {
				//log.info("   no: "+d);
			}
			count++;
		}
		return ld; //.toArray(new Double[]{});
	}
	
/*	public static void main(String[] args) throws Throwable {
		double[] list = new double[]{ 1, 2, 3.3, 10, -15};
		//List<Double> myList = new ArrayList<Double>();
		//Collections.addAll(myList, list); 
		//System.out.println("list: "+new ArrayList(Arrays.asList(list)));
		double min = min(list);
		double max = max(list);
		System.out.println("max: "+max);
		System.out.println("min: "+min);
		System.out.println("linear categories bounds: "+getLinearCategoriesLimits(min, max, 12));
		System.out.println("log categories bounds: "+getLogCategoriesLimits(min, max, 5));
	}*/
	
	public final static double[] toDoubleArray(Collection<Double> vals) {
		double[] rets = new double[vals.size()];
		int i = 0;
		for(Double val: vals) {
			rets[i] = val;
			i++;
		}
		return rets;
	}

	public final static List<Double> toDoubleList(double[] vals) {
		List<Double> rets = new ArrayList<Double>();
		for(double val: vals) {
			rets.add(val);
		}
		return rets;
	}
}
