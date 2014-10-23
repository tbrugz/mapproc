package tbrugz.mapproc.transform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import tbrugz.mapproc.Category;

public class Csv2Json {

	static Log log = LogFactory.getLog(Csv2Json.class);

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		genSeries();
		genCats();
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
		Kml2Json.indexedSeries2json(csvFile, csvWriter);
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

}
