package tbrugz.mapproc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tbrugz.stats.StatsUtils;
import tbrugz.xml.XmlPrinter;
import tbrugz.xml.XmlUtils;

public class LocalMain {
	static Log log = LogFactory.getLog(LocalMain.class);
	
	public static void main(String[] args) throws Exception {
		FileReader fr = new FileReader("work/input/tabela-municipios_e_habitantes.csv");
		BufferedReader br = new BufferedReader(fr);
		IndexedSerie is = new IndexedSerie();
		is.readFromStream(br);
		double[] vals = StatsUtils.toDoubleArray(is.getValues());
		List<Double> valsL = StatsUtils.toDoubleList(vals);

		//test...
		double min = StatsUtils.min(vals);
		double max = StatsUtils.max(vals);
		System.out.println("max: "+max);
		System.out.println("min: "+min);
		
		List<Double> limits = StatsUtils.getLogCategoriesLimits(min, max, 5);
		List<Category> cats = Category.getCategoriesFromLimits(limits);
		System.out.println("log categories bounds: "+limits);

		System.out.println("linear categories bounds: "+StatsUtils.getLinearCategoriesLimits(min, max, 5));
		//System.out.println("log categories bounds: "+StatsUtils.getLogCategoriesLimits(min, max, 5));
		System.out.println("percentile categories bounds: "+StatsUtils.getPercentileCategoriesLimits(valsL,  5));
		
		System.out.println("cats: "+cats);
		
		/*
		 * proc KLM:
		 * - define style for each category 
		 * - for each Placemark: 
		 *   - set description with value
		 *   - set category in style
		 * - draw categories (legends)
		 * 
		 */
		
		String kmlFile = "work/input/Municipalities_of_RS.kml";
		//KMLParser parser = new KMLParser();
		//StringWriter sw = new StringWriter();
		//parser.parseDocument(kmlFile, sw);
		
		//http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(kmlFile);
		doc.getDocumentElement().normalize();
		
		Properties snippets = new Properties();
		snippets.load(new FileInputStream("snippets.properties"));

		List<String> styles = getStylesFromCategories(cats, snippets);
		System.out.println(styles);
		
		//System.out.println("Root element :"	+ doc.getDocumentElement().getNodeName());
		
		//styles
		Element kmldoc = XmlUtils.getChildByTagName(doc.getDocumentElement(), "Document");
		int count = 0;
		Node kmlStylesPosBefore = kmldoc.getFirstChild();
		Node node1stFolder = XmlUtils.getChildByTagName(kmldoc, "Folder");
		if(node1stFolder!=null) {
			kmlStylesPosBefore = node1stFolder;
		}
		for(String styleStr: styles) {
			Element st = XmlUtils.getDocumentNodeFromString(styleStr, dBuilder).getDocumentElement();
			//Element st = doc.createElement("Style");
			//st.setTextContent(styleStr);
			//st.setAttribute("id", "style"+count);
			
			//see: http://stackoverflow.com/questions/883987/java-appending-xml-docs-to-existing-docs
			Node newNode = doc.importNode(st, true);
			kmldoc.insertBefore(newNode, kmlStylesPosBefore);
			//kmldoc.appendChild(newNode);
			count++;
		}

		//placemarks
		NodeList nList = doc.getElementsByTagName("Placemark");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				//System.out.println();
				String id = eElement.getAttribute("id");
				Double valueFromIS = is.getValue(id);
				
				//has entry
				if(valueFromIS!=null) {
					Category cat = Category.getCategoryFromValue(cats, valueFromIS);
					String styleId = cat!=null?cat.styleId:"NULL";
					Element eName = XmlUtils.getChildByTagName(eElement, "name");
					log.info("Placemark: value: "+valueFromIS+"; cat: "+cat+"; name: "+eName.getTextContent());
					
					//change description
					String desc = snippets.getProperty("description.append");
					desc = desc.replaceAll("\\{0\\}", String.valueOf(valueFromIS));
					Element descElem = XmlUtils.getChildByTagName(eElement, "description");
					if(descElem!=null) {
						descElem.setTextContent(descElem.getTextContent()+desc);
					}
					else {
						descElem = doc.createElement("description");
						descElem.setTextContent(desc);
						eElement.appendChild(descElem);
					}
					/*NodeList nl = eElement.getElementsByTagName("description");
					for (int j = 0; j < nl.getLength(); j++) {
						Node nNode2 = nl.item(j);
						if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement2 = (Element) nNode2;
							eElement2.setTextContent(eElement2.getTextContent()+desc);
						}
					}*/

					//change style
					Element styleElem = XmlUtils.getChildByTagName(eElement, "styleUrl");
					if(styleElem!=null) {
						styleElem.setTextContent("#style"+styleId);
					}
					else {
						//TODO: add style
					}

					/*NodeList nl3 = eElement.getElementsByTagName("styleUrl");
					for (int j = 0; j < nl3.getLength(); j++) {
						Node nNode2 = nl3.item(j);
						if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement2 = (Element) nNode2;
							eElement2.setTextContent("#style"+styleId);
						}
					}*/
					
				}
				/*
				System.out.println("First Name : "
						+ getTagValue("firstname", eElement));
				System.out.println("Last Name : "
						+ getTagValue("lastname", eElement));
				System.out.println("Nick Name : "
						+ getTagValue("nickname", eElement));
				System.out.println("Salary : "
						+ getTagValue("salary", eElement));
				*/
			}
		}
		
		//XmlPrinter.serialize(doc, System.out);
		XmlPrinter.serialize(doc, new FileWriter("work/output/Mun.kml"));
		//System.out.println("SW: "+sw);
		
		/*
		 * XSL? XPath? SAXParser?
		 * 
<Placemark id="mun_4309571">
<name>Herveiras</name>
<visibility>1</visibility>
<description>#id = mun_4309571</description>
<styleUrl>#style0</styleUrl>
<MultiGeometry><Polygon id="poly_mun_4309571"><outerBoundaryIs><LinearRing><coordinates>-52.77426,-29.443846,0 -52.777107,-29.437965,0 -52.771664,-29.423334,0 -52.761566,-29.418055,0 -52.77021,-29.412203,0 -52.75935,-29.40501,0 -52.752506,-29.399265,0 -52.74069,-29.40017,0 -52.73858,-29.396421,0 -52.736225,-29.400387,0 -52.726624,-29.399649,0 -52.72071,-29.394152,0 -52.704594,-29.400688,0 -52.676575,-29.397953,0 -52.667107,-29.392591,0 -52.661278,-29.395136,0 -52.653698,-29.389666,0 -52.64311,-29.397379,0 -52.62721,-29.421774,0 -52.61484,-29.425713,0 -52.61558,-29.43069,0 -52.612736,-29.42935,0 -52.60365,-29.442423,0 -52.604416,-29.44967,0 -52.59949,-29.44989,0 -52.596153,-29.454922,0 -52.59191,-29.453226,0 -52.58176,-29.465097,0 -52.572758,-29.465206,0 -52.568268,-29.469828,0 -52.58967,-29.47475,0 -52.608303,-29.473166,0 -52.613884,-29.48085,0 -52.608738,-29.486238,0 -52.615498,-29.492365,0 -52.68125,-29.499557,0 -52.68358,-29.485172,0 -52.67559,-29.468325,0 -52.6835,-29.470129,0 -52.68281,-29.46556,0 -52.689297,-29.463045,0 -52.690777,-29.452324,0 -52.703224,-29.45112,0 -52.70848,-29.444748,0 -52.71945,-29.44234,0 -52.723503,-29.433151,0 -52.733078,-29.43277,0 -52.737732,-29.438568,0 -52.743835,-29.435558,0 -52.773003,-29.440208,0 -52.77426,-29.443764,0 -52.77426,-29.443846,0 -52.77426,-29.443846,0 </coordinates></LinearRing></outerBoundaryIs></Polygon></MultiGeometry>
</Placemark>
		 * 
		 */
	}
	
	static List<String> getStylesFromCategories(List<Category> cats, Properties prop) {
		List<String> styles = new ArrayList<String>();
		List<Double> cores = StatsUtils.getLinearCategoriesLimits(0, 255, cats.size()-1);
		
		int i=0;
		for(Category c: cats) {
			String style = prop.getProperty("Style"); //0: id, 1: color
			style = style.replaceAll("\\{0\\}", c.styleId);
			style = style.replaceAll("\\{1\\}", "80"+hexString(cores.get(i).intValue())+"ffff");
			i++;
			styles.add(style);
		}
		return styles;
	}
	
	static String hexString(int i) {
		String s = Integer.toHexString(i);
		switch(s.length()) {
			case 1: return "0"+s;
			case 2: return s;
		}
		return s.substring(s.length()-2);
	}
	
	/*private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}*/
}
