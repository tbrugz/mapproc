package tbrugz.mapproc.transform;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import tbrugz.mapproc.Category;
import tbrugz.mapproc.IndexedSeries;
import static tbrugz.mapproc.transform.Kml2Json.QUOT; 

public class Csv2Json {

	static Log log = LogFactory.getLog(Csv2Json.class);

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		genSeries();
		//genCats();
	}

	static void genSeries() throws IOException {
		//series
		String[] series = {"area", "ha_por_area", "ha", "pib_por_area", "pib_por_ha", "pib"}; 
		//String[] series = {"votos_11", "votos_12", "votos_13", "votos_15", "votos_21", "votos_28", "votos_50", }; 
		for(String s: series) {
			gen1Serie(s);
		}
	}
	
	static void gen1Serie(String s) throws IOException {
		BufferedReader csvFile = new BufferedReader(new FileReader("work/input/csv/"+s+".csv"));
		String fout = "work/output/series-"+s+".json";
		FileWriter csvWriter = new FileWriter(fout);
		indexedSeries2json(csvFile, csvWriter);
		csvWriter.close();
		log.info("series writter to "+fout);
	}
	
	static void genCats() throws IOException {
		//categories
		BufferedReader csvCatFile = new BufferedReader(new FileReader("work/input/csvcat/tabela_categorias_vereadores-por-municipio.csv"));
		List<Category> cats = Category.getCategoriesFromCSVStream(csvCatFile, ";");
		FileWriter csvCatWriter = new FileWriter("work/output/cat-vereadores-mun.json");
		Kml2Json.categories2json(cats, csvCatWriter);
		csvCatWriter.close();
	}

	static void indexedSeries2json(BufferedReader reader, Writer os) throws IOException {
		IndexedSeries is = new IndexedSeries();
		is.readFromStream(reader);
		Set<String> keys = is.getKeys();
		
		int outCount = 0;
		os.write("{ \n");
		os.write("\t"+QUOT+"objectLabel"+QUOT+": "+getValue(is.metadata.objectLabel)+",\n");
		os.write("\t"+QUOT+"valueLabel"+QUOT+": "+getValue(is.metadata.valueLabel)+",\n");
		os.write("\t"+QUOT+"valueType"+QUOT+": "+QUOT+is.metadata.valueType+QUOT+",\n");
		os.write("\t"+QUOT+"measureUnit"+QUOT+": "+getValue(is.metadata.measureUnit)+",\n");
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
	
	static String getValue(String s) {
		return s==null?null:QUOT+s+QUOT;
	}
	

}
