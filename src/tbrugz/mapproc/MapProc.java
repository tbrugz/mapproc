package tbrugz.mapproc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.googlepages.aanand.dom.DOMUtilExt;

import tbrugz.stats.StatsUtils;
import tbrugz.stats.StatsUtils.ScaleType;
import tbrugz.xml.XmlPrinter;
import tbrugz.xml.DomUtils;

/*
 * ~TODO: placemark ordering by id, name or series-value (asc, desc)
 * !TODO: option to only output placemarks which have value
 * TODOne: categories from csv (description;startVal;endVal;styleId[;styleColor])
 * XXX: option to generate, or not, kml's <Styles>
 * TODOne: generate categories descriptions in a box next to the map
 * ~TODO: categories descriptions as folders...
 * TODO: add measure type to description
 * !TODO: option to generate categories after checking which values from series are valid (2nd pass needed)
 */
public class MapProc {
	static Log log = LogFactory.getLog(MapProc.class);
	
	final static String PROP_SNIPPETS = "/"+"snippets.properties";
	
	public static IndexedSeries getIndexedSeries(Reader dataSeriesReader) throws IOException {
		BufferedReader br = new BufferedReader(dataSeriesReader);
		IndexedSeries is = new IndexedSeries();
		is.readFromStream(br);
		return is;
	}
	
	void debug(double[] vals, int numOfCategories) {
		List<Double> valsL = StatsUtils.toDoubleList(vals);

		double min = StatsUtils.min(vals);
		double max = StatsUtils.max(vals);
		log.info("max: "+max);
		log.info("min: "+min);
		
		List<Double> limits = StatsUtils.getLogCategoriesLimits(min, max, numOfCategories);
		List<Category> cats = Category.getCategoriesFromLimits(limits);

		log.info("log categories bounds: "+limits);
		log.info("linear categories bounds: "+StatsUtils.getLinearCategoriesLimits(min, max, numOfCategories));
		//log.info("log categories bounds: "+StatsUtils.getLogCategoriesLimits(min, max, numOfCategories));
		log.info("percentile categories bounds: "+StatsUtils.getPercentileCategoriesLimits(valsL,  numOfCategories));
		
		log.info("cats: "+cats);
	}
		
	public void doIt(InputStream kmlURI, IndexedSeries is, Writer outputWriter, BufferedReader categoriesCsv, String colorFrom, String colorTo, boolean removeIfNotFound) throws IOException, ParserConfigurationException, SAXException {
		//double[] vals = StatsUtils.toDoubleArray(is.getValues());

		List<Category> cats = Category.getCategoriesFromCSVStream(categoriesCsv, ";");

		Properties snippets = new Properties();
		snippets.load(MapProc.class.getResourceAsStream(PROP_SNIPPETS));
		
		KmlUtils.procStylesFromCategories(cats, snippets, colorFrom, colorTo);
		
		doIt(kmlURI, is, outputWriter, cats, removeIfNotFound);
	}

	public void doIt(InputStream kmlURI, IndexedSeries is, Writer outputWriter, ScaleType scaleType, int numOfCategories, String colorFrom, String colorTo, boolean removeIfNotFound) throws IOException, ParserConfigurationException, SAXException {
		double[] vals = StatsUtils.toDoubleArray(is.getValues());

		List<Double> limits = StatsUtils.getCategoriesLimits(scaleType, StatsUtils.toDoubleList(vals), numOfCategories);
		List<Category> cats = Category.getCategoriesFromLimits(limits);

		Properties snippets = new Properties();
		snippets.load(MapProc.class.getResourceAsStream(PROP_SNIPPETS));
		
		KmlUtils.procStylesFromCategories(cats, snippets, colorFrom, colorTo);
		
		doIt(kmlURI, is, outputWriter, cats, removeIfNotFound);
	}

	public void doIt(InputStream kmlURI, IndexedSeries is, Writer outputWriter, ScaleType scaleType, int numOfCategories, String colorSpec, boolean removeIfNotFound) throws ParserConfigurationException, SAXException, IOException {
		double[] vals = StatsUtils.toDoubleArray(is.getValues());

		List<Double> limits = StatsUtils.getCategoriesLimits(scaleType, StatsUtils.toDoubleList(vals), numOfCategories);
		List<Category> cats = Category.getCategoriesFromLimits(limits);

		Properties snippets = new Properties();
		snippets.load(MapProc.class.getResourceAsStream(PROP_SNIPPETS));
		
		KmlUtils.procStylesFromCategories(cats, snippets, colorSpec);
		
		doIt(kmlURI, is, outputWriter, cats, removeIfNotFound);
	}
	
	//public void doIt(String kmlURI, Reader dataSeriesReader, Writer outputWriter, ScaleType scaleType, int numOfCategories, String colorSpec) throws Exception {
	void doIt(InputStream kmlStream, IndexedSeries is, Writer outputWriter, List<Category> cats, boolean removeIfNotFound) throws ParserConfigurationException, SAXException, IOException {
		//double[] vals = StatsUtils.toDoubleArray(is.getValues());
		
		//debug(vals, numOfCategories);

		//List<Double> limits = StatsUtils.getCategoriesLimits(scaleType, StatsUtils.toDoubleList(vals), numOfCategories);
		//List<Category> cats = Category.getCategoriesFromLimits(limits);
		//System.out.println("log categories bounds: "+limits);
		//System.out.println("cats: "+cats);
		
		/*
		 * proc KLM:
		 * - define style for each category 
		 * - for each Placemark: 
		 *   - set description with value
		 *   - set category in style
		 * - draw categories (legends)
		 * 
		 */
		
		//KMLParser parser = new KMLParser();
		//StringWriter sw = new StringWriter();
		//parser.parseDocument(kmlFile, sw);
		
		//http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(kmlStream);
		doc.getDocumentElement().normalize();
		
		Properties snippets = new Properties();
		//snippets.load(new FileInputStream("snippets.properties"));
		snippets.load(MapProc.class.getResourceAsStream(PROP_SNIPPETS));

		//List<String> styles = KmlUtils.getStylesFromCategories(cats, snippets, colorSpec);
		
		List<String> styles = KmlUtils.getStyleXMLsFromCategories(cats);
		//System.out.println(styles);
		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		
		//styles
		Element kmldoc = DomUtils.getChildByTagName(doc.getDocumentElement(), "Document");
		int count = 0;
		Node kmlStylesPosBefore = kmldoc.getFirstChild();
		Node node1stFolder = DomUtils.getChildByTagName(kmldoc, "Folder");
		if(node1stFolder!=null) {
			kmlStylesPosBefore = node1stFolder;
		}
		for(String styleStr: styles) {
			Element st = DomUtils.getDocumentNodeFromString(styleStr, dBuilder).getDocumentElement();
			//Element st = doc.createElement("Style");
			//st.setTextContent(styleStr);
			//st.setAttribute("id", "style"+count);
			
			//see: http://stackoverflow.com/questions/883987/java-appending-xml-docs-to-existing-docs
			Node newNode = doc.importNode(st, true);
			kmldoc.insertBefore(newNode, kmlStylesPosBefore);
			//kmldoc.appendChild(newNode);
			count++;
		}

		Element placemarksFolder = null;
		//placemarks
		int placemarkCount = 0;
		NodeList nList = doc.getElementsByTagName("Placemark");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				//container of first placemark is used for placemark sorting
				if(placemarksFolder==null) {
					placemarksFolder = (Element) eElement.getParentNode();
				}
				
				String id = eElement.getAttribute("id");
				if(id==null || id.equals("")) {
					id = newId(eElement, false);
				}
				Double valueFromIS = is.getValue(id);
				
				//has entry
				if(valueFromIS!=null) {
					Category cat = Category.getCategoryFromValue(cats, valueFromIS);
					String styleId = cat!=null?cat.styleId:"NULL";
					Element eName = DomUtils.getChildByTagName(eElement, "name");
					log.debug("Placemark: value: "+valueFromIS+"; cat: "+cat+"; name: "+eName.getTextContent());
					
					//change description
					String desc = snippets.getProperty("description.append");
					desc = desc.replaceAll("\\{0\\}", is.metadata.valueLabel);
					desc = desc.replaceAll("\\{1\\}", String.valueOf(valueFromIS));
					Element descElem = DomUtils.getChildByTagName(eElement, "description");
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
					Element styleElem = DomUtils.getChildByTagName(eElement, "styleUrl");
					if(styleElem!=null) {
						styleElem.setTextContent("#style"+styleId);
					}
					else {
						//XXX: add style?
						log.warn("no 'style' found [id="+id+"]");
					}

				}
				else {
					//TODO: remove if not found?
					//log.warn("id '"+id+"' not found in data");
					if(removeIfNotFound) {
						eElement.getParentNode().removeChild(eElement);
					}
				}
				placemarkCount++;
			}
		}
		
		//NodeList newNodeList = doc.getElementsByTagName("Placemark");
		//if(newNodeList.getLength()<=0) {
		if(placemarkCount==0) {
			log.warn("no nodes to serialize");
			return;
		}

		//KmlUtils kmlBoundUtils = new KmlUtils();
		/*kmlBounds.grabMinMaxLatLong(doc.getDocumentElement());
		String boundsCoords = kmlBounds.getBoundsCoordinates(-1);
		String categoriesStr = snippets.getProperty("Categories.Feature");
		log.info("boundsCoords: "+boundsCoords);
		categoriesStr = categoriesStr.replaceAll("\\{0\\}", boundsCoords);
		
		Element catElem = DomUtils.getDocumentNodeFromString(categoriesStr, dBuilder).getDocumentElement();
		Node catElemNew = doc.importNode(catElem, true);
		kmldoc.appendChild(catElemNew);*/
		KmlUtils.addCategoriesLabels(doc, kmldoc, snippets.getProperty("Categories.Feature"), cats, snippets.getProperty("Categories.Elem"), is.metadata, dBuilder);
		
		//DOMUtilExt.sortChildNodes(placemarksFolder, false, 1, new DOMUtilExt.IdAttribComparator());
		
		XmlPrinter.serialize(doc, outputWriter);
	}
	
	static String newId(Element eElement, boolean setId) {
		Element eName = DomUtils.getChildByTagName(eElement, "name");
		if(eName!=null) {
			String id = eName.getTextContent();
			if(setId) { eElement.setAttribute("id", id); }
			return id;
		}
		else {
			log.warn("placemark: object id not found");
		}
		return null;
	}
	
}
