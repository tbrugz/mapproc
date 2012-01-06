package tbrugz.mapproc.transform;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
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
		//maps
		int[] idEstados = {35, 43};
		for(int idEstado: idEstados) {
			FileInputStream kmlFile = new FileInputStream("work/input/kml/"+idEstado+"Mun.kml");
			FileWriter writer = new FileWriter("work/output/map-"+idEstado+"-mun.json");
			kml2json(kmlFile, writer);
			writer.close();
		}
				
		//series
		String[] series = {"area", "ha_por_area", "ha", "pib_por_area", "pib_por_ha", "pib"}; 
		for(String s: series) {
			BufferedReader csvFile = new BufferedReader(new FileReader("work/input/csv/"+s+".csv"));
			FileWriter csvWriter = new FileWriter("work/output/series-"+s+".json");
			indexedSeries2json(csvFile, csvWriter);
			csvWriter.close();
		}
		
		//categories
		/*
		BufferedReader csvCatFile = new BufferedReader(new FileReader("work/input/csvcat/tabela_categorias_vereadores-por-municipio.csv"));
		List<Category> cats = Category.getCategoriesFromCSVStream(csvCatFile, ";");
		FileWriter csvCatWriter = new FileWriter("work/output/cat-vereadores-mun.json");
		categories2json(cats, csvCatWriter);
		csvCatWriter.close();
		*/
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
			String coords = getTagContent(eElement, "coordinates");
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
			pp.coordinates = newCoords;
			pols.add(pp);
		}
		
		int outCount = 0;
		os.write("{");
		//os.write("[ \n");
		for(PolygonPlacemark pp: pols) {
			if(pp.name!=null && !pp.name.equals("")) {
				os.write((outCount!=0?",":"")+ 
						"\n\t"+QUOT+pp.id+QUOT+": "+pp.getJSON());
				//os.write("\t"+pp.getJSON()+",\n");
				outCount++;
			}
		}
		os.write("\n}");
		//os.write("]");
		
		log.info("wrote "+outCount+" placemarks");
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
