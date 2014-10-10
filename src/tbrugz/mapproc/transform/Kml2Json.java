package tbrugz.mapproc.transform;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import tbrugz.mapproc.Category;
import tbrugz.mapproc.IndexedSeries;
import tbrugz.xml.DomUtils;

public class Kml2Json {
	static Log log = LogFactory.getLog(Kml2Json.class);
	final static String QUOT = "\"";
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		int[] idEstados = {35, 43};
		genMaps(idEstados);
	}
	
	static void genMaps(int[] idEstados) throws SAXException, IOException, ParserConfigurationException {
		//maps
		for(int idEstado: idEstados) {
			log.info("idEstado: "+idEstado);
			FileInputStream kmlFile = new FileInputStream("work/input/kml/"+idEstado+"Mun.kml");
			String jsonfile = "work/output/geojs-"+idEstado+"-mun.json";
			FileWriter writer = new FileWriter(jsonfile);
			
			kml2json(kmlFile, writer);
			writer.close();
			log.info("wrote file: "+jsonfile);
		}
	}
	
	static void kml2json(InputStream is, Writer os) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		Document doc = dBuilder.parse(is);

		List<PolygonPlacemark> pols = new ArrayList<PolygonPlacemark>();
		
		//placemarks
		NodeList nList = doc.getElementsByTagName("Placemark");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			Element eElement = (Element) nNode;
			PolygonPlacemark pp = new PolygonPlacemark();
			pp.id = eElement.getAttribute("id");
			pp.name = getTagContent(eElement, "name");
			pp.description = getTagContent(eElement, "description");
			
			NodeList coordsl = eElement.getElementsByTagName("coordinates");
			
			for(int ij=0;ij<coordsl.getLength(); ij++) {
				String coords = ((Element)coordsl.item(ij)).getTextContent();;
				
				String[] coord = coords.split("\\s+");
				String newCoords = "";
				for(int j=0;j<coord.length;j++) {
					String ss = coord[j];
					String s[] = ss.split(",");
					if(s.length>1) {
						newCoords += (j==0?"":", ") + "["+s[1]+", "+s[0]+"]";
						//newCoords += (j==0?"":", ") + "["+s[0]+", "+s[1]+"]";
					}
					else {
						log.warn("ss: "+ss);
					}
				}
				pp.coordinates.add( newCoords );
			}
			
			pols.add(pp);
		}
		
		//XXX: geojson or "previous-json"?
		//int outCount = writeJson(pols, os);
		int outCount = writeGeoJson(pols, os);
		
		log.info("wrote "+outCount+" placemarks");
	}
	
	static int writeJson(List<PolygonPlacemark> pols, Writer os) throws IOException {
		os.write("{");
		//os.write("[ \n");
		int outCount = 0;
		for(PolygonPlacemark pp: pols) {
			if(pp.name!=null && !pp.name.equals("")) {
				os.write((outCount!=0?",":"")+ 
						"\n\t"+QUOT+pp.id+QUOT+": "+pp.getJSON());
				//os.write("\t"+pp.getJSON()+",\n");
				outCount++;
			}
		}
		os.write("\n}");
		return outCount;
		//os.write("]");
		
	}

	/*
	 * see: http://en.wikipedia.org/wiki/GeoJSON
	 */
	static int writeGeoJson(List<PolygonPlacemark> pols, Writer os) throws IOException {
		os.write("{ "+QUOT+"type"+QUOT+": "+QUOT+"FeatureCollection"+QUOT+", "+QUOT+"features"+QUOT+": [ ");
		int outCount = 0;
		for(PolygonPlacemark pp: pols) {
			if(pp.name!=null && !pp.name.equals("")) {
				os.write((outCount!=0?",":"")+ 
						"\n\t"+pp.getGeoJSON());
				outCount++;
			}
		}
		os.write("\n] }");
		return outCount;
	}
	
	static String getTagContent(Element element, String tagname) {
		Element innerElem = DomUtils.getChildByTagName(element, tagname);
		if(innerElem!=null) {
			return innerElem.getTextContent();
		}
		return null;
	}

	static void indexedSeries2json(BufferedReader reader, Writer os) throws IOException {
		IndexedSeries is = new IndexedSeries();
		is.readFromStream(reader);
		Set<String> keys = is.getKeys();
		
		int outCount = 0;
		os.write("{ \n");
		os.write("\t"+QUOT+"objectLabel"+QUOT+": "+QUOT+""+is.metadata.objectLabel+""+QUOT+",\n");
		os.write("\t"+QUOT+"valueLabel"+QUOT+": "+QUOT+""+is.metadata.valueLabel+""+QUOT+",\n");
		os.write("\t"+QUOT+"valueType"+QUOT+": "+QUOT+""+is.metadata.valueType+""+QUOT+",\n");
		os.write("\t"+QUOT+"measureUnit"+QUOT+": "+QUOT+""+is.metadata.measureUnit+""+QUOT+",\n");
		os.write("\t"+QUOT+"series"+QUOT+": {\n");
		for(String key: keys) {
			//XXX: only works for integer/float values
			os.write((outCount!=0?",\n":"")
					+"\t\t"+QUOT+key+QUOT+": "+is.getValue(key));
			outCount++;
		}
		os.write("\n\t}\n}");

		log.info("wrote "+outCount+" elements");
	}
	
	static void categories2json(List<Category> cats, Writer os) throws IOException {
		int outCount = 0;
		os.write("{\n");
		for(Category c: cats) {
			//XXX: only works for integer/float values
			os.write((outCount!=0?",":"")
					+"\n\t"+QUOT+c.getStyleId()+QUOT+": {\n"
					+"\t\t"+QUOT+"startval"+QUOT+": "+c.getStartVal()+",\n"
					+"\t\t"+QUOT+"endval"+QUOT+": "+c.getEndVal()+",\n"
					+"\t\t"+QUOT+"kmlcolor"+QUOT+": "+QUOT+""+c.getStyleColor()+""+QUOT+",\n"
					+"\t\t"+QUOT+"description"+QUOT+": "+QUOT+""+c.getDescription()+""+QUOT+"\n"
					+"\t\t}"
					);
			outCount++;
		}
		os.write("\n}");

		log.info("[cat] wrote "+outCount+" elements");
	}
	
}
