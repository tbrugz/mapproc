package tbrugz.mapproc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tbrugz.mapproc.MapProc;
import tbrugz.stats.StatsUtils.ScaleType;

public class MapProcTest {

	static Log log = LogFactory.getLog(MapProcTest.class);

	FileReader seriesFile;
	//BufferedReader catsFile = new BufferedReader(new FileReader("work/input/tabela_categorias_vereadores-por-municipio.csv"));
	BufferedReader catsFile;
	InputStream kmlFile;
	FileWriter outputWriter;

	int numOfCategories = 5;
	ScaleType scaleType = ScaleType.LOG;
	String colorFrom = "aaff0000"; String colorTo = "aa0000ff";
	String colorSpec = "a000++00";
	
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
	
	@Test
	public void testGeneratedCategories() throws IOException, ParserConfigurationException, SAXException {
		kmlFile = new FileInputStream("work/test/input/kml/RSSimple.kml");
		seriesFile = new FileReader("work/test/input/csv/tabela-municipios_e_habitantes-parcial-5-RS.csv");
		outputWriter = new FileWriter("work/output/MunFull.kml");
		
		MapProc lm = new MapProc();
		Document doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, false);
		NodeList nList = doc.getElementsByTagName("Placemark");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			//System.out.println("id\t"+((Element)nNode).getAttribute("id"));
			if (nNode.getNodeType() != Node.ELEMENT_NODE) {
				fail("all nodes shoul be Node.ELEMENT_NODE: "+nNode);
			}
		}
		log.info("size: "+nList.getLength()+"; cats: "+numOfCategories);
		assertEquals(5 + numOfCategories + 1, nList.getLength());

		kmlFile = new FileInputStream("work/input/kml/Municipalities_of_RS.kml");
		seriesFile = new FileReader("work/test/input/csv/tabela-municipios_e_habitantes-parcial-100sorted-RS.csv");
		outputWriter = new FileWriter("work/output/MunFull.kml");
		
		lm = new MapProc();
		doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, false);
		nList = doc.getElementsByTagName("Placemark");
		log.info("size: "+nList.getLength()+"; cats: "+numOfCategories);
		assertEquals(497 + numOfCategories + 1, nList.getLength());

	}

	@Test
	public void testGeneratedCategoriesPartialSeries() throws IOException, ParserConfigurationException, SAXException {
		kmlFile = new FileInputStream("work/test/input/kml/RSSimple.kml");
		seriesFile = new FileReader("work/test/input/csv/tabela-municipios_e_habitantes-parcial-5-RS.csv");
		outputWriter = new FileWriter("work/output/MunPartial.kml");
		
		MapProc lm = new MapProc();
		Document doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true);
		NodeList nList = doc.getElementsByTagName("Placemark");
		log.info("partial size: "+nList.getLength());
		assertEquals(4 + numOfCategories + 1, nList.getLength());

		kmlFile = new FileInputStream("work/input/kml/Municipalities_of_RS.kml");
		seriesFile = new FileReader("work/test/input/csv/tabela-municipios_e_habitantes-parcial-495-RS.csv");
		outputWriter = new FileWriter("work/output/MunPartial.kml");
		
		lm = new MapProc();
		doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true);
		nList = doc.getElementsByTagName("Placemark");
		log.info("partial size: "+nList.getLength());
		assertEquals(495 + numOfCategories + 1, nList.getLength());

		kmlFile = new FileInputStream("work/input/kml/Municipalities_of_RS.kml");
		seriesFile = new FileReader("work/test/input/csv/tabela-municipios_e_habitantes-parcial-100sorted-RS.csv");
		outputWriter = new FileWriter("work/output/MunPartial.kml");
		
		lm = new MapProc();
		doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true);
		nList = doc.getElementsByTagName("Placemark");
		log.info("partial size: "+nList.getLength());
		/*for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			System.out.println("id\t"+((Element)nNode).getAttribute("id"));
		}*/
		assertEquals(100 + numOfCategories + 1, nList.getLength());

		kmlFile = new FileInputStream("work/input/kml/Municipalities_of_RS.kml");
		seriesFile = new FileReader("work/test/input/csv/tabela-municipios_e_habitantes-parcial-100-RS.csv");
		outputWriter = new FileWriter("work/output/MunPartial.kml");
		
		lm = new MapProc();
		doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true);
		nList = doc.getElementsByTagName("Placemark");
		log.info("size: "+nList.getLength()+"; cats: "+numOfCategories);
		assertEquals(100 + numOfCategories + 1, nList.getLength());
	}
	
	//@Test
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
		MapProc lm = new MapProc();
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorSpec, false);
	}

}
