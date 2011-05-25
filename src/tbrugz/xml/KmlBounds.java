package tbrugz.xml;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tbrugz.mapproc.Category;
import tbrugz.mapproc.IndexedSeriesMetadata;
import tbrugz.mapproc.LocalMain;
import tbrugz.stats.StatsUtils;

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
		/*return maxLong+","+maxLat+","+altitude+" "+
			maxLong+","+minLat+","+altitude+" "+
			minLong+","+minLat+","+altitude+" "+
			minLong+","+maxLat+","+altitude+" "+
			maxLong+","+maxLat+","+altitude+" ";*/
	}

	public static String getBoundsCoordinates(double maxLong, double minLong, double maxLat, double minLat, double altitude) {
		return maxLong+","+maxLat+","+altitude+" "+
			maxLong+","+minLat+","+altitude+" "+
			minLong+","+minLat+","+altitude+" "+
			minLong+","+maxLat+","+altitude+" "+
			maxLong+","+maxLat+","+altitude+" ";
	}
	
	public void addCategoriesLabels(Document doc, Element kmlElem, String catLabelsSnippet, List<Category> cats, String catElemSnippet, IndexedSeriesMetadata isMetadata, DocumentBuilder dBuilder) throws SAXException, IOException {
		grabMinMaxLatLong(doc.getDocumentElement());
		//put it right from features
		double longDist = (maxLong-minLong)/10;
		double latDist = (maxLat-minLat)/4;
		double leftLong = maxLong + longDist; 
		double rightLong = maxLong + longDist*2;
		double topLat = maxLat - latDist;
		double bottomLat = minLat + latDist;
		
		String boundsCoords = getBoundsCoordinates(rightLong, leftLong, topLat, bottomLat, 0);
		catLabelsSnippet = catLabelsSnippet.replaceAll("\\{0\\}", boundsCoords);

		Element catLabelsElem = DomUtils.getDocumentNodeFromString(catLabelsSnippet, dBuilder).getDocumentElement();
		Node catLabelsElemNew = doc.importNode(catLabelsElem, true);
		kmlElem.appendChild(catLabelsElemNew);
		
		List<Double> yVals = StatsUtils.getLinearCategoriesLimits(bottomLat, topLat, cats.size()*2+1);
		List<Double> xVals = StatsUtils.getLinearCategoriesLimits(leftLong, rightLong, 3);
		yVals.remove(yVals.size()-1);
		yVals.remove(0);
		
		double left = xVals.get(1);
		double right = xVals.get(2);

		int i=0;
		for(Category c: cats) {
			double bottom = yVals.get(i);
			double top = yVals.get(i+1);

			String catId = c.getStyleId(); //XXX: +1 (test to see if it is an int)
			String catBoundsCoords = getBoundsCoordinates(right, left, top, bottom, 0);
			String catDesc = 
				isMetadata.format(c.getStartVal()) + 
				" &lt; " + "# "+isMetadata.valueLabel + " &lt; " +
				isMetadata.format(c.getEndVal()); //c.getDescription();
			
			String catElemStr = catElemSnippet.replaceAll("\\{0\\}", catId);
			catElemStr = catElemStr.replaceAll("\\{1\\}", catDesc);
			catElemStr = catElemStr.replaceAll("\\{2\\}", catBoundsCoords);
			Element catElem = DomUtils.getDocumentNodeFromString(catElemStr, dBuilder).getDocumentElement();
			Node catElemNew = doc.importNode(catElem, true);
			catLabelsElemNew.appendChild(catElemNew);
			
			i+=2;
		}
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
