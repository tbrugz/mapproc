package tbrugz.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KmlBounds {
	
	static Log log = LogFactory.getLog(KmlBounds.class);
	double 
		maxLat,
		minLat,
		maxLong,
		minLong;

	{
		resetMaxMins();
	}
	
	public void resetMaxMins() {
		maxLat = -Double.MAX_VALUE;
		minLat = Double.MAX_VALUE;
		maxLong = -Double.MAX_VALUE;
		minLong = Double.MAX_VALUE;
	}
	
	public void grabMinMaxLatLong(Element kmlElem) {
		NodeList nl = kmlElem.getElementsByTagName("coordinates");
		for (int i = 0; i < nl.getLength(); i++) {
			Node nNode = nl.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String s = eElement.getTextContent();
				String[] points = s.split("\\s+");
				for(String pointStr: points) {
					String[] longLatAlt = pointStr.split(",");
					double dLong = Double.parseDouble(longLatAlt[0]);
					double dLat = Double.parseDouble(longLatAlt[1]);
					//double dAlt = Double.parseDouble(longLatAlt[2]);
					
					setMinMaxLatLong(dLat, dLong);
				}
			}
		}
	}

	void setMinMaxLatLong(double dLat, double dLong) {
		if(dLong > maxLong) maxLong = dLong;
		if(dLong < minLong) minLong = dLong;
		if(dLat > maxLat) maxLat = dLat;
		if(dLat < minLat) minLat = dLat;
		//log.debug("lat: "+dLat+", long: "+dLong+", maxLat: "+maxLat+", maxLong: "+maxLong);
	}
	
	/*
	 * z-order?
	 * http://groups.google.com/group/kml-support-advanced/browse_thread/thread/cd7f3b19eb7b68d0 
	 */
	public String getBoundsCoordinates(double altitude) {
		return KmlBounds.getBoundsCoordinates(maxLong, minLong, maxLat, minLat, altitude);
	}

	public static String getBoundsCoordinates(double maxLong, double minLong, double maxLat, double minLat, double altitude) {
		return maxLong+","+maxLat+","+altitude+" "+
			maxLong+","+minLat+","+altitude+" "+
			minLong+","+minLat+","+altitude+" "+
			minLong+","+maxLat+","+altitude+" "+
			maxLong+","+maxLat+","+altitude+" ";
	}
	
	public double getMaxLat() {
		return maxLat;
	}

	public double getMinLat() {
		return minLat;
	}

	public double getMaxLong() {
		return maxLong;
	}

	public double getMinLong() {
		return minLong;
	}
	
}
