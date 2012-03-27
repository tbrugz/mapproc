package tbrugz.mapproc.transform;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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

import tbrugz.mapproc.MapProc;
import tbrugz.stats.StatsUtils;
import tbrugz.xml.DomUtils;
import tbrugz.xml.XmlPrinter;

class LngLat {
	double lng, lat;
}

public class MapProcBatch {
	static Log log = LogFactory.getLog(MapProcBatch.class);

	public final static String PROP_SNIPPETS = "/"+"snippets.properties";
	
	FileReader seriesFile;
	BufferedReader catsFile;
	InputStream kmlFile;
	//FileWriter outputWriter;
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		MapProcBatch mpb = new MapProcBatch();
		//mpb.splitKmlByState();
		Properties p = new Properties();
		p.load(new FileInputStream("dados/mapping/municipios-estado.properties"));
		mpb.groupKmlPolygons(p);
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
	
	void splitKmlByState() throws ParserConfigurationException, SAXException, IOException {
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

	void groupKmlPolygons(Map<Object, Object> map) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		//kmlFile = new FileInputStream("shapefiles/55mu2500gsd.kml");
		kmlFile = new FileInputStream("work/input/AllNormMun2.kml");
		Document doc = dBuilder.parse(kmlFile);

		Set<String> groups = new HashSet<String>(); 
		for(Object o: map.keySet()) {
			groups.add((String) map.get(o));
		}
		
		log.info("start group proc [#group="+groups.size()+"]");
		
		for(String group: groups) {
			//int idEstado=11;idEstado<=60;idEstado++
			
			int countElem = 0;
	
			FileInputStream baseKmlFile = new FileInputStream("work/input/baseKml.kml");
				
			List<List<LngLat>> groupPoints = new ArrayList<List<LngLat>>();
			
			//placemarks
			NodeList nList = doc.getElementsByTagName("Placemark");
			for (int i = 0; i < nList.getLength(); i++) {
				Element eElement = (Element) nList.item(i);
				String id = eElement.getAttribute("id");
				
				//log.info("elem: "+id+" group: "+map.get(id));
				if(map.get(id)==null) { continue; }

				if(map.get(id).equals(group)) {
					//eElement.getElementsByTagName("styleUrl").item(0).setTextContent("#styleNULL");
					String coords = eElement.getElementsByTagName("coordinates").item(0).getTextContent();
					List<LngLat> lls = getPoints(coords);
					groupPoints.add(lls);
					//outFolder.appendChild(outDoc.importNode(eElement, true));
					log.debug("ok: id: "+id+"; group: "+group);
					countElem++;
				}
			}
	
			if(countElem==0) { 
				log.info("0 mun on group = "+group);
				continue;
			}
			
			/* TODO: Para cada 4 pontos, ver se 4 ponto est� "dentro" do triangulo formado pelos outros 3. 
			 *       Se tiver, deve ser removido
			 * 
			 * http://en.wikipedia.org/wiki/Clipping_%28computer_graphics%29
			 * http://en.wikipedia.org/wiki/Boolean_operations_on_polygons#External_links
			 * http://stackoverflow.com/questions/4229425/how-do-i-find-the-overlapping-area-between-two-arbitrary-polygons
			 * http://www.complex-a5.ru/polyboolean/comp.html
			 */
			
			List<LngLat> bounds = getPolygon(groupPoints);
			String coordinates = getCoordinates(bounds);

			Properties snippets = new Properties();
			snippets.load(MapProc.class.getResourceAsStream(PROP_SNIPPETS));
			String placemark = snippets.getProperty("generic-placemark");
			placemark = placemark.replaceAll("\\{id\\}", Matcher.quoteReplacement(group) );
			placemark = placemark.replaceAll("\\{name\\}", Matcher.quoteReplacement(group) );
			placemark = placemark.replaceAll("\\{desc\\}", Matcher.quoteReplacement(group) );
			placemark = placemark.replaceAll("\\{coordinates\\}", coordinates );
			//placemark = placemark.replaceAll("\\{style\\}", "#style..." );

			Document outDoc = dBuilder.parse(baseKmlFile);
			NodeList nListzz = outDoc.getElementsByTagName("Folder");
			Element outFolder = (Element) nListzz.item(0);
			Element eElement = DomUtils.getDocumentNodeFromString(placemark, dBuilder).getDocumentElement();
			outFolder.appendChild(outDoc.importNode(eElement, true));
			
			String filename = "work/output/t1/"+group+".kml";
			FileWriter outputWriter = new FileWriter(filename);
			log.info("group="+group+"; count="+countElem);
			XmlPrinter.serialize(outDoc, outputWriter);
			log.info("count="+countElem+"; wrote: "+filename);
			
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
	
	static List<LngLat> getPoints(String coords) {
		String points[] = coords.split("\\s+");
		List<LngLat> l = new ArrayList<LngLat>();
		for(String s: points) {
			String s2[] = s.split(",");
			LngLat ll = new LngLat();
			ll.lng = Double.parseDouble(s2[0]);
			ll.lat = Double.parseDouble(s2[1]);
			l.add(ll);
		}
		return l;
	}
	
	static List<LngLat> getPolygon(List<List<LngLat>> points) {
		List<LngLat> flatlist = new ArrayList<LngLat>();
		for(List<LngLat> l: points) {
			flatlist.addAll(l);
		}
		return getPolygonSimple(flatlist);
	}
	
	static List<LngLat> getPolygonSimple(List<LngLat> points) {
		//TODO: get max/min lat/long - then go clockwise
		//InitialVersion: just get the 4 extreme points
		double[] lat = new double[points.size()];
		double[] lng = new double[points.size()];
		List<LngLat> ret = new ArrayList<LngLat>();
		
		for(int i=0;i<points.size();i++) {
			LngLat ll = points.get(i);
			lat[i] = ll.lat;
			lng[i] = ll.lng;
		}

		ret.add(points.get(StatsUtils.maxIndex(lat)));
		ret.add(points.get(StatsUtils.minIndex(lng)));
		ret.add(points.get(StatsUtils.minIndex(lat)));
		ret.add(points.get(StatsUtils.maxIndex(lng)));
		
		return ret;
	}
	
	static String getCoordinates(List<LngLat> list) {
		//-62.1820888570,-11.8668597878,0 -62.1622953938,-11.8713991426,0
		StringBuffer sb = new StringBuffer();
		for(LngLat ll: list) {
			sb.append(ll.lng+","+ll.lat+",0 ");
		}
		return sb.toString();
	}
	
	static String noId(String id, Element eElement) {
		Element eName = DomUtils.getChildByTagName(eElement, "name");
		if(eName!=null) {
			id = "mun_"+eName.getTextContent();
			eElement.setAttribute("id", id);
			//&lt;font COLOR="#008000"&gt;�gua Branca&lt;/font&gt
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
