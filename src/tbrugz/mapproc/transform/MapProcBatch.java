package tbrugz.mapproc.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import tbrugz.xml.DomUtils;
import tbrugz.xml.XmlPrinter;

public class MapProcBatch {
	static Log log = LogFactory.getLog(MapProcBatch.class);

	public final static String PROP_SNIPPETS = "/"+"snippets.properties";
	
	final static String BASE_KML_PATH = "work/input/baseKml.kml";
	final static String ALL_PLACEMARKS_PATH = "work/input/AllNormMun2.kml";
	
	FileReader seriesFile;
	BufferedReader catsFile;
	InputStream kmlFile;
	//FileWriter outputWriter;
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		MapProcBatch mpb = new MapProcBatch();

		//--- split by mapping ---
		////Properties p = getMunicipios();
		//Properties p = new Properties();
		//p.load(new FileInputStream("work/input/mapping/municipios-mesorregiao.properties"));
		////municipios.store(new FileOutputStream("work/output/municipios-estado.properties"), "");
		//mpb.splitKmlByMapping(p);


		//--- group plygons ---
		Properties p = new Properties();
		p.load(new FileInputStream("work/input/mapping/municipios-estado.properties"));
		mpb.groupKmlPolygons("estados", p, false);
		
		p.load(new FileInputStream("work/input/mapping/municipios-mesorregiao.properties"));
		mpb.groupKmlPolygons("mesorregioes", p, true);
		
		//p.load(new FileInputStream("work/input/mapping/municipios-microrregiao.properties"));
		//mpb.groupKmlPolygons("microrregioes", p, false);


		//--- normalize ---
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
	
	void splitKmlByMapping(String kmlname, Map<Object, Object> map) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		//kmlFile = new FileInputStream("shapefiles/55mu2500gsd.kml");
		kmlFile = new FileInputStream(ALL_PLACEMARKS_PATH);
		Document doc = dBuilder.parse(kmlFile);

		Set<String> groups = getValuesSet(map);
		
		log.info("splitKmlByMapping init");
		
		for(String group: groups) {
		//for(int idEstado=11;idEstado<=60;idEstado++) {
			
			int countElem = 0;
	
			FileInputStream baseKmlFile = new FileInputStream(BASE_KML_PATH);
			Document outDoc = dBuilder.parse(baseKmlFile);
			setAllTagTextByTagName(outDoc, "name", kmlname);
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
					
					if(map.get(id)==null) { continue; }
					if(map.get(id).equals(group)) {
						//root3.appendChild(doc3.importNode(root1, true));
						eElement.getElementsByTagName("styleUrl").item(0).setTextContent("#styleNULL");
						outFolder.appendChild(outDoc.importNode(eElement, true));
						//id = noId(id, eElement);
						log.debug("ok: id: "+id+"; group: "+group);
						countElem++;
					}
				}
			}
			
			/*NodeList newNodeList = doc.getElementsByTagName("Placemark");
			if(newNodeList.getLength()<=0) {
				log.warn("no nodes to serialize [i="+idEstado+"]");
				continue;
			}*/
	
			if(countElem==0) { 
				log.info("0 elements on group = "+group);
				continue;
			}
			//DOMUtilExt.sortChildNodes(placemarksFolder, false, 1, new DOMUtilExt.IdAttribComparator());
			
			String filename = "work/output/t1/"+kmlname+"_"+group+".kml";
			FileWriter outputWriter = new FileWriter(filename);
			log.info("group id = "+group+"; count="+countElem);
			XmlPrinter.serialize(outDoc, outputWriter);
			log.info("wrote to '"+new File(filename).getAbsolutePath()+"'");
			
			outDoc = null;
			nList = null;
			System.gc();
		}
	}

	//TODO: add param: name mapping
	void groupKmlPolygons(String kmlname, Map<Object, Object> map, boolean uniqueKml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		//String baseKmlFileStr = "work/input/baseKml.kml";

		//kmlFile = new FileInputStream("shapefiles/55mu2500gsd.kml");
		kmlFile = new FileInputStream(ALL_PLACEMARKS_PATH);
		Document doc = dBuilder.parse(kmlFile);

		Set<String> groups = getValuesSet(map);
		
		log.info("start group proc [#group="+groups.size()+"]");
		
		Properties groupCoordinatesElement = new Properties();
		
		for(String group: groups) {
			int countElem = 0;

			//XXX: clone 'outDoc'
			FileInputStream baseKmlFile = new FileInputStream(BASE_KML_PATH);
				
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
			
			/* TODO: Para cada 4 pontos, ver se 4 ponto está "dentro" do triangulo formado pelos outros 3. 
			 *       Se tiver, deve ser removido
			 * 
			 * http://en.wikipedia.org/wiki/Clipping_%28computer_graphics%29
			 * http://en.wikipedia.org/wiki/Boolean_operations_on_polygons#External_links
			 * http://stackoverflow.com/questions/4229425/how-do-i-find-the-overlapping-area-between-two-arbitrary-polygons
			 * http://www.complex-a5.ru/polyboolean/comp.html
			 */
			
			//PolygonGrouper pg = new LatLongMinMaxPolygonGrouper();
			PolygonGrouper pg = new PolygonGrouper.ConvexHullPolygonGrouper();
			List<LngLat> bounds = pg.getPolygon(groupPoints);
			String coordinates = getCoordinates(bounds);

			Properties snippets = new Properties();
			snippets.load(MapProc.class.getResourceAsStream(PROP_SNIPPETS));
			String placemark = snippets.getProperty("generic-placemark");
			placemark = placemark.replaceAll("\\{id\\}", Matcher.quoteReplacement(group) );
			placemark = placemark.replaceAll("\\{name\\}", Matcher.quoteReplacement(group) );
			placemark = placemark.replaceAll("\\{desc\\}", Matcher.quoteReplacement(group) );
			placemark = placemark.replaceAll("\\{coordinates\\}", coordinates );
			//placemark = placemark.replaceAll("\\{style\\}", "#style..." );

			log.info("polygon grouper: id="+group+"; #elements="+countElem+"; #bounds="+bounds.size());
			
			if(!uniqueKml) {
				writeKml(dBuilder, baseKmlFile, kmlname, placemark, group);
			}
			else {
				groupCoordinatesElement.setProperty(group, placemark);
			}
			nList = null;
			System.gc();
		}

		if(uniqueKml) {
			Document outDoc = dBuilder.parse(new FileInputStream(BASE_KML_PATH));
			setAllTagTextByTagName(outDoc, "name", kmlname);
			NodeList nListzz = outDoc.getElementsByTagName("Folder");
			Element outFolder = (Element) nListzz.item(0);

			for(Object groupId: groupCoordinatesElement.keySet()) {
				//writeKml(dBuilder, new FileInputStream(baseKmlFileStr), groupCoordinatesElement.getProperty((String)groupId), (String)groupId);
				
				Element eElement = DomUtils.getDocumentNodeFromString(groupCoordinatesElement.getProperty((String)groupId), dBuilder).getDocumentElement();
				outFolder.appendChild(outDoc.importNode(eElement, true));
				
			}
			String filename = "work/output/t1/"+kmlname+".kml";
			FileWriter outputWriter = new FileWriter(filename);
			XmlPrinter.serialize(outDoc, outputWriter);
			
			log.info("wrote to '"+new File(filename).getAbsolutePath()+"'");
		}
		
		log.info("end group proc [#group="+groups.size()+"]");
	}
	
	void writeKml(DocumentBuilder dBuilder, FileInputStream baseKmlFile, String kmlname, String placemark, String group) throws SAXException, IOException {
		Document outDoc = dBuilder.parse(baseKmlFile);
		setAllTagTextByTagName(outDoc, "name", kmlname+"_"+group);
		NodeList nListzz = outDoc.getElementsByTagName("Folder");
		Element outFolder = (Element) nListzz.item(0);
		Element eElement = DomUtils.getDocumentNodeFromString(placemark, dBuilder).getDocumentElement();
		outFolder.appendChild(outDoc.importNode(eElement, true));
		
		String filename = "work/output/t1/"+kmlname+"_"+group+".kml";
		FileWriter outputWriter = new FileWriter(filename);
		XmlPrinter.serialize(outDoc, outputWriter);
		log.info("wrote to '"+new File(filename).getAbsolutePath()+"'");
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
	
	static Set<String> getValuesSet(Map<Object,Object> map) {
		Set<String> set = new HashSet<String>();
		for(Object o: map.keySet()) {
			set.add((String)map.get(o));
		}
		return set;
	}
	
	static Properties getMunicipios() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		p.load(new FileInputStream("work/input/mapping/municipios-mesorregiao.properties"));
		Properties ret = new Properties();
		for(Object o: p.keySet()) {
			String id = (String) o;
			ret.setProperty(id, id.substring(0, 2));
		}
		return ret;
	}
	
	static void setAllTagTextByTagName(Document doc, String tag, String text) {
		NodeList nlist = doc.getElementsByTagName(tag);
		for(int i=0;i<nlist.getLength();i++) {
			Element e = (Element) nlist.item(i);
			e.setTextContent(text);
		}
	}
	
}
