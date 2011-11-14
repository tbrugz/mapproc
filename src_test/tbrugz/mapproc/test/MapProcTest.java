package tbrugz.mapproc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tbrugz.mapproc.Category;
import tbrugz.mapproc.IndexedSeries;
import tbrugz.mapproc.KmlUtils;
import tbrugz.mapproc.MapProc;
import tbrugz.stats.StatsUtils;
import tbrugz.stats.StatsUtils.ScaleType;

public class MapProcTest {

	static Log log = LogFactory.getLog(MapProcTest.class);

	FileReader seriesFile;
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
		Document doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, false, false);
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
		doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, false, false);
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
		Document doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true, false);
		NodeList nList = doc.getElementsByTagName("Placemark");
		log.info("partial size: "+nList.getLength());
		assertEquals(4 + numOfCategories + 1, nList.getLength());

		kmlFile = new FileInputStream("work/input/kml/Municipalities_of_RS.kml");
		seriesFile = new FileReader("work/test/input/csv/tabela-municipios_e_habitantes-parcial-495-RS.csv");
		outputWriter = new FileWriter("work/output/MunPartial.kml");
		
		lm = new MapProc();
		doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true, false);
		nList = doc.getElementsByTagName("Placemark");
		log.info("partial size: "+nList.getLength());
		assertEquals(495 + numOfCategories + 1, nList.getLength());

		kmlFile = new FileInputStream("work/input/kml/Municipalities_of_RS.kml");
		seriesFile = new FileReader("work/test/input/csv/tabela-municipios_e_habitantes-parcial-100sorted-RS.csv");
		outputWriter = new FileWriter("work/output/MunPartial.kml");
		
		lm = new MapProc();
		doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true, false);
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
		doc = lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true, false);
		nList = doc.getElementsByTagName("Placemark");
		log.info("size: "+nList.getLength()+"; cats: "+numOfCategories);
		assertEquals(100 + numOfCategories + 1, nList.getLength());
	}

	@Test
	public void testGeneratedCategoriesLimits() throws IOException, ParserConfigurationException, SAXException {
		String kmlIn = "work/test/input/kml/RSSimple.kml"; //"work/input/kml/43Mun.kml";
		String csvIn = "work/test/input/csv/tabela-municipios_e_habitantes-parcial-5-RS.csv"; //"work/input/csv/pib.csv";
		String kmlOut = "work/output/MunSimple.kml";
		
		kmlFile = new FileInputStream(kmlIn);
		seriesFile = new FileReader(csvIn);
		outputWriter = new FileWriter(kmlOut);
		IndexedSeries is = MapProc.getIndexedSeries(seriesFile);
		MapProc lm = new MapProc();
		
		Document doc = lm.doIt(kmlFile, is, outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true, false);
		double[] vals = StatsUtils.toDoubleArray(is.getValues());
		long min = Math.round(StatsUtils.min(vals));
		long max = Math.round(StatsUtils.max(vals));
		assertEquals(12, min);
		assertEquals(20000, max);

		kmlFile = new FileInputStream(kmlIn);
		seriesFile = new FileReader(csvIn);
		outputWriter = new FileWriter(kmlOut);
		
		doc = lm.doIt(kmlFile, is, outputWriter, scaleType, numOfCategories, colorFrom, colorTo, true, true);
		vals = StatsUtils.toDoubleArray(StatsUtils.getValsForExistingPlacemarks(is, doc));
		min = Math.round(StatsUtils.min(vals));
		max = Math.round(StatsUtils.max(vals));
		assertEquals(123, min);
		assertEquals(12312, max);

	}
	
	@Test
	public void testCatFromCSVStream() throws IOException {
		BufferedReader catsFile = new BufferedReader(new FileReader("work/input/csvcat/tabela_categorias_vereadores-por-municipio.csv"));
		String csvCatOut = "work/output/ColorCat.csv";
		outputWriter = new FileWriter(csvCatOut);
		String sep = ";";
		List<Category> cats = Category.getCategoriesFromCSVStream(catsFile, ";");

		Properties snippets = new Properties();
		snippets.load(MapProc.class.getResourceAsStream(MapProc.PROP_SNIPPETS));

		KmlUtils.procStylesFromCategories(cats, snippets, colorFrom, colorTo);
		//TODO: dump cat
		outputWriter.write("XXX;MIN;MAX;COLOR\n");
		for(Category cat: cats) {
			outputWriter.write(cat.getStyleId()+sep+cat.getStartVal()+sep+cat.getEndVal()+sep+cat.getStyleColor()+"\n");
		}
		outputWriter.close();
	}
	
	public void testCatsGen() throws IOException {
		//List<Double> limits = StatsUtils.getCategoriesLimits(scaleType, StatsUtils.toDoubleList(vals), numOfCategories);
		//List<Category> cats = Category.getCategoriesFromLimits(limits);
		
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
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorFrom, colorTo, false, false);
	}

	//@Test
	public void testGeneratedCategoriesPattern() throws IOException, ParserConfigurationException, SAXException {
		MapProc lm = new MapProc();
		lm.doIt(kmlFile, MapProc.getIndexedSeries(seriesFile), outputWriter, scaleType, numOfCategories, colorSpec, false, false);
	}

}
