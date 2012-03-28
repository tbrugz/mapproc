package tbrugz.mapproc.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tbrugz.mapproc.transform.GrahamScan;
import tbrugz.mapproc.transform.GrahamScan.Point;

public class PolygonGrouperTest {

	@Test
	public void testGrahamScan() {
		GrahamScan gs = new GrahamScan();
		
		List<Point> pp = getPoints02();
		
		List<Point> ordered = gs.order(pp);
		System.out.println("pp: "+pp+"\nor: "+ordered);
		
		List<Point> ch = gs.findHull(ordered);
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
	
}
