package tbrugz.mapproc;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import tbrugz.stats.StatsUtils;
import tbrugz.xml.DomUtils;
import tbrugz.xml.KmlBounds;

public class KmlUtils {
	
	public void addCategoriesLabels(Document doc, Element kmlElem, String catLabelsSnippet, List<Category> cats, String catElemSnippet, IndexedSeriesMetadata isMetadata, DocumentBuilder dBuilder) throws SAXException, IOException {
		KmlBounds kmlbounds = new KmlBounds();
		
		kmlbounds.grabMinMaxLatLong(doc.getDocumentElement());
		//put it right from features
		double longDist = (kmlbounds.getMaxLong()-kmlbounds.getMinLong())/10;
		double latDist = (kmlbounds.getMaxLat()-kmlbounds.getMinLat())/4;
		double leftLong = kmlbounds.getMaxLong() + longDist; 
		double rightLong = kmlbounds.getMaxLong() + longDist*2;
		double topLat = kmlbounds.getMaxLat() - latDist;
		double bottomLat = kmlbounds.getMinLat() + latDist;
		
		String boundsCoords = KmlBounds.getBoundsCoordinates(rightLong, leftLong, topLat, bottomLat, 0);
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
			String catBoundsCoords = KmlBounds.getBoundsCoordinates(right, left, top, bottom, 0);
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

}
