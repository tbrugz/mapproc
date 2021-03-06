package tbrugz.mapproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import tbrugz.stats.StatsUtils;
import tbrugz.xml.DomUtils;
import tbrugz.xml.KmlBounds;

public class KmlUtils {
	static Log log = LogFactory.getLog(KmlUtils.class);
	
	static void addCategoriesLabels(Document doc, Element kmlElem, String catLabelsSnippet, String catLabelsContainerSnippet, List<Category> cats, String catElemSnippet, IndexedSeriesMetadata isMetadata, DocumentBuilder dBuilder) throws SAXException, IOException {
		KmlBounds kmlbounds = new KmlBounds();
		
		kmlbounds.grabMinMaxLatLong(doc.getDocumentElement());
		//put it right from features
		double longDist = (kmlbounds.getMaxLong()-kmlbounds.getMinLong())/10;
		double latDist = (kmlbounds.getMaxLat()-kmlbounds.getMinLat())/4;
		double leftLong = kmlbounds.getMaxLong() + longDist; 
		double rightLong = kmlbounds.getMaxLong() + longDist*2;
		double topLat = kmlbounds.getMaxLat() - latDist;
		double bottomLat = kmlbounds.getMinLat() + latDist;
		
		//String boundsCoords = KmlBounds.getBoundsCoordinates(rightLong, leftLong, topLat, bottomLat, -1); //-1 for being below other labels
		//catLabelsSnippet = catLabelsSnippet.replaceAll("\\{0\\}", Matcher.quoteReplacement(boundsCoords) );

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

			//Character Entity References, see: http://www.elizabethcastro.com/html/extras/entities.html
			String catId = c.getStyleId(); //XXX: +1 (test to see if it is an int)
			String catName = c.getName();
			String catBoundsCoords = KmlBounds.getBoundsCoordinates(right, left, top, bottom, 0);
			String catDesc = 
				isMetadata.format(c.getStartVal()) + 
				" &lt; " + "# "+isMetadata.valueLabel + " &lt; " + //&#8804;
				isMetadata.format(c.getEndVal()); //c.getDescription();
			
			String catElemStr = catElemSnippet.replaceAll("\\{id\\}", Matcher.quoteReplacement(catId) );
			catElemStr = catElemStr.replaceAll("\\{name\\}", Matcher.quoteReplacement(catName) );
			catElemStr = catElemStr.replaceAll("\\{description\\}", Matcher.quoteReplacement(catDesc) );
			catElemStr = catElemStr.replaceAll("\\{coordinates\\}", Matcher.quoteReplacement(catBoundsCoords) );
			Element catElem = DomUtils.getDocumentNodeFromString(catElemStr, dBuilder).getDocumentElement();
			Node catElemNew = doc.importNode(catElem, true);
			catLabelsElemNew.appendChild(catElemNew);
			
			i+=2;
		}
		
		if(catLabelsContainerSnippet!=null) {
			String boundsCoords = KmlBounds.getBoundsCoordinates(rightLong, leftLong, topLat, bottomLat, -1); //-1: putting below other labels
			catLabelsContainerSnippet = catLabelsContainerSnippet.replaceAll("\\{0\\}", Matcher.quoteReplacement(boundsCoords) );
	
			Element catLabelsContainerElem = DomUtils.getDocumentNodeFromString(catLabelsContainerSnippet, dBuilder).getDocumentElement();
			Node catLabelsContainerNew = doc.importNode(catLabelsContainerElem, true);
			catLabelsElemNew.appendChild(catLabelsContainerNew);
		}
	}
	
	@Deprecated
	static void procStylesFromCategories(List<Category> cats, Properties prop, String colorSpec) {
		if(colorSpec==null || colorSpec.length()!=8) {
			throw new RuntimeException("ColorSpec must be in format 'aabbggrr'; '++' and '--' are used for color substitution");
		}
		
		//List<String> styles = new ArrayList<String>();
		List<Double> colors = StatsUtils.getLinearCategoriesLimits(0, 255, cats.size()-1);
		
		/*
		 * color format is 'aabbggrr', see: http://code.google.com/apis/kml/documentation/kmlreference.html#colorstyle
		 */
			
		//String colorSpec = a0++ffff, a0--ffff
		//String colorSpec = "a0--ffff";
		//String colorSpec = "a0++ffff";
		
		int i=0;
		for(Category c: cats) {
			String style = prop.getProperty("Style"); //0: id, 1: color
			style = style.replaceAll("\\{0\\}", Matcher.quoteReplacement(c.styleId));
			
			String positiveHex = hexString( colors.get(i).intValue() );
			String complementHex = hexString( complFF( colors.get(i).intValue() ) );
			
			String color = colorSpec.replaceAll("\\+\\+", positiveHex);
			color = color.replaceAll("\\-\\-", complementHex);
			log.debug("colorSpec: "+colorSpec+"; cat: "+c.styleId+"; color: "+color);
			
			//style = style.replaceAll("\\{1\\}", "a0"+hex+"ffff");
			style = style.replaceAll("\\{1\\}", color);
			c.styleColor = color;
			i++;
			c.styleXML = style;
			//styles.add(style);
		}
		//return styles;
	}

	static void procStylesXMLFromCategories(List<Category> cats, Properties prop) {
		/*
		 * color format is 'aabbggrr', see: http://code.google.com/apis/kml/documentation/kmlreference.html#colorstyle
		 */
		
		for(Category c: cats) {
			String style = prop.getProperty("Style"); //0: id, 1: color
			style = style.replaceAll("\\{0\\}", Matcher.quoteReplacement(c.styleId));
			style = style.replaceAll("\\{1\\}", Matcher.quoteReplacement(c.styleColor));
			c.styleXML = style;
		}
	}
	
	public static void procStylesFromCategories(List<Category> cats, Properties prop, String colorFrom, String colorTo) {
		if(colorFrom==null || colorFrom.length()!=8) {
			throw new RuntimeException("ColorFrom must be in format 'aabbggrr'");
		}

		if(colorTo==null || colorTo.length()!=8) {
			throw new RuntimeException("ColorTo must be in format 'aabbggrr'");
		}
		
		//List<String> styles = new ArrayList<String>();
		
		List<Double> colorsA = StatsUtils.getLinearCategoriesLimits(Integer.parseInt(colorFrom.substring(0, 2), 16), Integer.parseInt(colorTo.substring(0, 2), 16), cats.size()-1);
		List<Double> colorsB = StatsUtils.getLinearCategoriesLimits(Integer.parseInt(colorFrom.substring(2, 4), 16), Integer.parseInt(colorTo.substring(2, 4), 16), cats.size()-1);
		List<Double> colorsG = StatsUtils.getLinearCategoriesLimits(Integer.parseInt(colorFrom.substring(4, 6), 16), Integer.parseInt(colorTo.substring(4, 6), 16), cats.size()-1);
		List<Double> colorsR = StatsUtils.getLinearCategoriesLimits(Integer.parseInt(colorFrom.substring(6, 8), 16), Integer.parseInt(colorTo.substring(6, 8), 16), cats.size()-1);
		
		/*
		 * color format is 'aabbggrr', see: http://code.google.com/apis/kml/documentation/kmlreference.html#colorstyle
		 */
		
		int i=0;
		for(Category c: cats) {
			String style = prop.getProperty("Style"); //0: id, 1: color
			style = style.replaceAll("\\{0\\}", Matcher.quoteReplacement(c.styleId) );
			
			//String positiveHex = hexString( colors.get(i).intValue() );
			//String complementHex = hexString( complFF( colors.get(i).intValue() ) );
	
			//String color = "";
			log.debug("colorFrom: "+colorFrom+"; colorTo: "+colorTo+"; cat: "+c.styleId+"; colorNow:"+c.styleColor);

			if(c.styleColor==null) {
				c.styleColor = hexString(colorsA.get(i).intValue()) + hexString(colorsB.get(i).intValue()) + hexString(colorsG.get(i).intValue()) + hexString(colorsR.get(i).intValue());
				log.debug("new Color:: colorFrom: "+colorFrom+"; colorTo: "+colorTo+"; cat: "+c.styleId+"; colorNow:"+c.styleColor);
			}
			//style = style.replaceAll("\\{1\\}", "a0"+hex+"ffff");
			style = style.replaceAll("\\{1\\}", Matcher.quoteReplacement(c.styleColor) );
			//c.styleColor = color;
			i++;
			c.styleXML = style;
			//styles.add(style);
		}
		//return styles;
	}

	static List<String> getStyleXMLsFromCategories(List<Category> cats) {
		List<String> styles = new ArrayList<String>();
		
		for(Category c: cats) {
			styles.add(c.styleXML);
		}
		return styles;
	}
	
	static int complFF(int i) {
		return 255-i;
	}
	
	static String hexString(int i) {
		String s = Integer.toHexString(i);
		switch(s.length()) {
			case 1: return "0"+s; //padding
			case 2: return s;
		}
		return s.substring(s.length()-2); //reminder (like '%')
	}

}
