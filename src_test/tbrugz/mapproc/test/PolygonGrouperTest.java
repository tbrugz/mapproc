package tbrugz.mapproc.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tbrugz.mapproc.transform.BorderTravelerGrouper;
import tbrugz.mapproc.transform.GrahamScan;
import tbrugz.mapproc.transform.GrahamScan.Point;
import tbrugz.mapproc.transform.LngLat;
import tbrugz.mapproc.transform.MapProcBatch;

public class PolygonGrouperTest {

	@Test
	public void testGrahamScan() {
		List<Point> pp = getPoints02();
		
		List<Point> ordered = GrahamScan.order(pp);
		System.out.println("pp: "+pp+"\nor: "+ordered);
		
		List<Point> ch = GrahamScan.findHull(ordered);
		System.out.println("pp: "+pp+"\nch: "+ch);
	}
	
	List<Point> getPoints01() {
		List<Point> pp = new ArrayList<GrahamScan.Point>();
		pp.add(new Point(0, 1));
		pp.add(new Point(1, 0));
		pp.add(new Point(0, -1));
		pp.add(new Point(-0.9, -1.1)); //lower-left point
		pp.add(new Point(-1, 0));
		return pp;
	}
	
	List<Point> getPoints02() {
		List<Point> pp = getPoints01();
		pp.add(new Point(0.5, 0.5));
		pp.add(new Point(-0.5, 0.5));
		return pp;
	}

	@Test
	public void testDirection() {
		Point pBase = new Point(0, 1);
		Point pInt = new Point(0, 2);
		Point p3 = new Point(-1, 3);
		Point p4 = new Point(1, 3);
		
		//p3 is base point, p2 is new Point
		// > 0  is -right- (left!) turn
		// == 0 is collinear
		// < 0  is -left- (right!) turn
		
		{
			double direction = GrahamScan.direction(pBase, pInt, p3);
			System.out.println("p1p2p3: "+pBase+", "+pInt+", "+p3+": turn = "+direction+" [left]");
			Assert.assertTrue(direction>0);
		}

		{
			double direction = GrahamScan.direction(pBase, pInt, p4);
			System.out.println("p1p2p3: "+pBase+", "+pInt+", "+p4+": turn = "+direction+" [right]");
			Assert.assertTrue(direction<0);
		}
	}
	
	@Test
	public void testBorderTraveler01() {
		List<LngLat> l1 = new ArrayList<LngLat>();
		List<LngLat> l2 = new ArrayList<LngLat>();
		List<LngLat> l3 = new ArrayList<LngLat>();
		
		l1.add(new LngLat(0, 0));
		l1.add(new LngLat(0, 1));
		l1.add(new LngLat(1, 0));

		l2.add(new LngLat(1, 1));
		l2.add(new LngLat(0, 1));
		l2.add(new LngLat(1, 0));

		l3.add(new LngLat(0, 1));
		l3.add(new LngLat(0.5, 1.5));
		l3.add(new LngLat(1, 1));
		
		List<List<LngLat>> ll = new ArrayList<List<LngLat>>();
		ll.add(l1); ll.add(l2); ll.add(l3);
		
		BorderTravelerGrouper btg = new BorderTravelerGrouper();
		List<LngLat> lret = btg.getPolygon(ll);
		
		System.out.println("l1: "+l1+"\nl2: "+l2+"\nl3: "+l3+"\nll: "+lret);
	}

	@Test
	public void testBorderTraveler02() {
		List<LngLat> l1 = new ArrayList<LngLat>();
		List<LngLat> l2 = new ArrayList<LngLat>();
		List<LngLat> l3 = new ArrayList<LngLat>();
		List<LngLat> l4 = new ArrayList<LngLat>();
		
		l1.add(new LngLat(0, 0));
		l1.add(new LngLat(0, 1));
		l1.add(new LngLat(1, 0));
		l1.add(new LngLat(1, 1));

		l2.add(new LngLat(1, 1));
		l2.add(new LngLat(1, 0));
		l2.add(new LngLat(2, 1));
		l2.add(new LngLat(2, 0));

		l3.add(new LngLat(1, 1));
		l3.add(new LngLat(1, 2));
		l3.add(new LngLat(2, 2));
		l3.add(new LngLat(2, 1));

		l4.add(new LngLat(0, 1)); //l4.add(new LngLat(0, 1.5));
		l4.add(new LngLat(0, 2));
		l4.add(new LngLat(1, 1));
		l4.add(new LngLat(1, 2));
		
		List<List<LngLat>> ll = new ArrayList<List<LngLat>>();
		ll.add(l1); ll.add(l2); ll.add(l3); ll.add(l4);
		
		BorderTravelerGrouper btg = new BorderTravelerGrouper();
		List<LngLat> lret = btg.getPolygon(ll);
		
		System.out.println("l1: "+l1+"\nl2: "+l2+"\nl3: "+l3+"\nll: "+lret);
	}
	
	@Test
	public void testBorderTraveler03() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		FileInputStream kmlFile = new FileInputStream("work/input/kml/12Mun.kml");
		Document doc = dBuilder.parse(kmlFile);
		
		//log.info("start group proc [#group="+groups.size()+"]");
		
		List<List<LngLat>> ll = new ArrayList<List<LngLat>>();
		NodeList nList = doc.getElementsByTagName("coordinates");
		for (int i = 0; i < nList.getLength(); i++) {
			Element eElement = (Element) nList.item(i);
			List<LngLat> l = MapProcBatch.getLngLatList(eElement.getTextContent()); 
			System.out.println("l["+i+"]: "+l);
			ll.add( l );
		}

		BorderTravelerGrouper btg = new BorderTravelerGrouper();
		List<LngLat> lret = btg.getPolygon(ll);
		System.out.println("lret[size="+lret.size()+"]: "+lret);
	}
	
}
