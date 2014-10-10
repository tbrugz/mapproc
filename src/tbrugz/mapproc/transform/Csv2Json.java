package tbrugz.mapproc.transform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tbrugz.mapproc.Category;

public class Csv2Json {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		genSeries();
		genCats();
	}

	static void genSeries() throws IOException {
		//series
		String[] series = {"area", "ha_por_area", "ha", "pib_por_area", "pib_por_ha", "pib"}; 
		for(String s: series) {
			BufferedReader csvFile = new BufferedReader(new FileReader("work/input/csv/"+s+".csv"));
			FileWriter csvWriter = new FileWriter("work/output/series-"+s+".json");
			Kml2Json.indexedSeries2json(csvFile, csvWriter);
			csvWriter.close();
		}
		
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
