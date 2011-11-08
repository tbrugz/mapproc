package tbrugz.mapproc.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import tbrugz.mapproc.MapProc;
import tbrugz.stats.StatsUtils.ScaleType;

public class MapProcTest {

	FileReader seriesFile;
	//BufferedReader catsFile = new BufferedReader(new FileReader("work/input/tabela_categorias_vereadores-por-municipio.csv"));
	BufferedReader catsFile;
	InputStream kmlFile;
	FileWriter outputWriter;
	
	@Before
	public void before() throws IOException {
		seriesFile = new FileReader("work/input/csv/tabela-municipios_e_habitantes-RS.csv");
		//BufferedReader catsFile = new BufferedReader(new FileReader("work/input/tabela_categorias_vereadores-por-municipio.csv"));
		//catsFile = new BufferedReader(new FileReader("work/input/csvcat/tabela_categorias_vereadores-por-municipio-color.csv"));
		kmlFile = new FileInputStream("work/input/kml/Municipalities_of_RS.kml");
		outputWriter = new FileWriter("work/output/Mun.kml");
	}
	
	//@Test
	public void testCategoriesFile() throws IOException, ParserConfigurationException, SAXException {
		String colorFrom = "aaff0000"; String colorTo = "aa0000ff";
		
		MapProc lm = new MapProc();
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, catsFile, colorFrom, colorTo, false);
	}
	
	//@Test
	public void testGeneratedCategories() throws IOException, ParserConfigurationException, SAXException {
		int numOfCategories = 5;
		ScaleType scaleType = ScaleType.LOG;
		String colorFrom = "aaff0000"; String colorTo = "aa0000ff";
		
		MapProc lm = new MapProc();
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, false);
	}
	
	@Test
	public void testGeneratedCategories02() throws IOException, ParserConfigurationException, SAXException {
		int numOfCategories = 5;
		ScaleType scaleType = ScaleType.LOG;
		String colorFrom = "aaff0000"; String colorTo = "aa0000ff";
		seriesFile = new FileReader("work/input/csv/area.csv");
		kmlFile = new FileInputStream("work/input/kml/43Mun.kml");
		outputWriter = new FileWriter("work/output/43Mun.kml");
		
		MapProc lm = new MapProc();
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, false);
	}

	//@Test
	public void testGeneratedCategoriesPattern() throws IOException, ParserConfigurationException, SAXException {
		int numOfCategories = 5;
		ScaleType scaleType = ScaleType.LOG;
		String colorSpec = "a000++00";
		
		MapProc lm = new MapProc();
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorSpec, false);
	}

}
