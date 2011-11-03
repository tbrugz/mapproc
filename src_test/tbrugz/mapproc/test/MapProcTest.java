package tbrugz.mapproc.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	String kmlFile;
	FileWriter outputWriter;
	
	@Before
	public void before() throws IOException {
		seriesFile = new FileReader("work/input/tabela-municipios_e_habitantes.csv");
		//BufferedReader catsFile = new BufferedReader(new FileReader("work/input/tabela_categorias_vereadores-por-municipio.csv"));
		catsFile = new BufferedReader(new FileReader("work/input/tabela_categorias_vereadores-por-municipio-color.csv"));
		kmlFile = "work/input/Municipalities_of_RS.kml";
		outputWriter = new FileWriter("work/output/Mun.kml");
	}
	
	@Test
	public void testCategoriesFile() throws IOException, ParserConfigurationException, SAXException {
		String colorFrom = "aaff0000"; String colorTo = "aa0000ff";
		
		MapProc lm = new MapProc();
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, catsFile, colorFrom, colorTo);
	}
	
	@Test
	public void testGeneratedCategories() throws IOException, ParserConfigurationException, SAXException {
		int numOfCategories = 5;
		ScaleType scaleType = ScaleType.LOG;
		String colorFrom = "aaff0000"; String colorTo = "aa0000ff";
		
		MapProc lm = new MapProc();
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo);
	}
}
