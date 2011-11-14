package tbrugz.mapproc.transform;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import tbrugz.xml.DomUtils;
import tbrugz.xml.XmlPrinter;

public class MapProcBatch {
	static Log log = LogFactory.getLog(MapProcBatch.class);

	FileReader seriesFile;
	BufferedReader catsFile;
	InputStream kmlFile;
	//FileWriter outputWriter;
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		MapProcBatch mpb = new MapProcBatch();
		mpb.doIt();
		//mpb.normalize();
		//mpb.normalizeAll();
	}

	void normalize() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		kmlFile = new FileInputStream("shapefiles/55mu2500gsd.kml");
		Document doc = dBuilder.parse(kmlFile);
		doc.getDocumentElement().normalize();
		
		//DOMUtilExt.sortChildNodes(placemarksFolder, false, 1, new DOMUtilExt.IdAttribComparator());
		
		String filename = "work/output/allMun.kml";
		FileWriter outputWriter = new FileWriter(filename);
		XmlPrinter.serialize(doc, outputWriter);
		System.out.println("wrote: "+filename);
		
	}
	
	void doIt() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		//kmlFile = new FileInputStream("shapefiles/55mu2500gsd.kml");
		kmlFile = new FileInputStream("work/input/AllNormMun2.kml");
		Document doc = dBuilder.parse(kmlFile);

		for(int idEstado=11;idEstado<=60;idEstado++) {
			
			int countMun = 0;
	
			FileInputStream baseKmlFile = new FileInputStream("work/input/baseKml.kml");
			Document outDoc = dBuilder.parse(baseKmlFile);
			NodeList nListzz = outDoc.getElementsByTagName("Folder");
			Element outFolder = (Element) nListzz.item(0);
				
			//placemarks
			NodeList nList = doc.getElementsByTagName("Placemark");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//System.out.println();
					String id = eElement.getAttribute("id");
					/*if(id==null || id.equals("")) {
						id = getId(id, eElement);
					}*/
					
					//container of first placemark is used for placemark sorting
					/*if(placemarksFolder==null) {
						placemarksFolder = (Element) eElement.getParentNode();
					}*/
					
					//String startWith = "mun_"+idEstado;
					String startWith = ""+idEstado;
					
					if(!id.startsWith(startWith)) {
						//Node n = placemarksFolder.removeChild(eElement);
						//log.info("removed: id: "+id+"; startWith: "+startWith);//+"; n: "+n);
						
						//doc.removeChild(oldChild)
						//nList.
					}
					else {
						//root3.appendChild(doc3.importNode(root1, true));
						eElement.getElementsByTagName("styleUrl").item(0).setTextContent("#styleNULL");
						outFolder.appendChild(outDoc.importNode(eElement, true));
						//id = noId(id, eElement);
						log.debug("ok: id: "+id+"; startWith: "+startWith);
						countMun++;
					}
				}
			}
			
			/*NodeList newNodeList = doc.getElementsByTagName("Placemark");
			if(newNodeList.getLength()<=0) {
				log.warn("no nodes to serialize [i="+idEstado+"]");
				continue;
			}*/
	
			if(countMun==0) { 
				log.info("0 mun on state = "+idEstado);
				continue;
			}
			//DOMUtilExt.sortChildNodes(placemarksFolder, false, 1, new DOMUtilExt.IdAttribComparator());
			
			String filename = "work/output/t1/"+idEstado+"Mun.kml";
			FileWriter outputWriter = new FileWriter(filename);
			log.info("idEstado="+idEstado+"; count="+countMun);
			XmlPrinter.serialize(outDoc, outputWriter);
			log.info("count="+countMun+"; wrote: "+filename);
			
			outDoc = null;
			nList = null;
			System.gc();
		}
	}

	void normalizeAll() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		kmlFile = new FileInputStream("shapefiles/55mu2500gsd.kml");
		Document doc = dBuilder.parse(kmlFile);
		doc.getDocumentElement().normalize();
			
		Element placemarksFolder = null;
		//placemarks
		NodeList nList = doc.getElementsByTagName("Placemark");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				//System.out.println();
				String id = eElement.getAttribute("id");
				if(id==null || id.equals("")) {
					id = getId(id, eElement);
				}
				
				//container of first placemark is used for placemark sorting
				if(placemarksFolder==null) {
					placemarksFolder = (Element) eElement.getParentNode();
				}
				else {
					id = noId(id, eElement);
				}
			}
		}
		
		//DOMUtilExt.sortChildNodes(placemarksFolder, false, 1, new DOMUtilExt.IdAttribComparator());
		
		String filename = "work/output/AllNormMun.kml";
		FileWriter outputWriter = new FileWriter(filename);
		XmlPrinter.serialize(doc, outputWriter);
		System.out.println("wrote: "+filename);
	}
	
	static String noId(String id, Element eElement) {
		Element eName = DomUtils.getChildByTagName(eElement, "name");
		if(eName!=null) {
			id = "mun_"+eName.getTextContent();
			eElement.setAttribute("id", id);
			//&lt;font COLOR="#008000"&gt;Água Branca&lt;/font&gt
			Element eDescr = DomUtils.getChildByTagName(eElement, "description");
			String desc = eDescr.getTextContent();
			Matcher matcher = Pattern.compile("#008000\">(.*)</font>").matcher(desc);
			
			boolean matchFound = matcher.find();
			if(matchFound) {
				String name = matcher.group(1);
				eName.setTextContent(name);
				eDescr.setTextContent(name);
			}
			else {
				eDescr.setTextContent("");
			}
		}
		else {
			log.warn("placemark: object id not found");
		}
		return id;
	}

	static String getId(String id, Element eElement) {
		Element eName = DomUtils.getChildByTagName(eElement, "name");
		if(eName!=null) {
			return "mun_"+eName.getTextContent();
		}
		else {
			log.warn("placemark: object id not found");
		}
		return null;
	}
	
}
