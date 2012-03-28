package tbrugz.mapproc.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Adapted from: http://efnx.com/as3-creating-a-convex-polygon-from-unordered-points/
 * 
 * The Graham scan is a method of computing the convex hull of a finite set of
 * points in the plane with time complexity O(n log n). It is named after Ronald
 * Graham, who published the original algorithm in 1972. The algorithm finds all
 * vertices of the convex hull ordered along its boundary. It may also be easily
 * modified to report all input points that lie on the boundary of their convex
 * hull.
 */
public class GrahamScan {

	public static class Point {
		double x, y;
		double cot; // cotangent

		public Point() {
		}

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public Point(double x, double y, double cot) {
			this.x = x;
			this.y = y;
			this.cot = cot;
		}

		@Override
		public String toString() {
			// return "["+x+","+y+"/"+cot+"]";
			return "[" + x + ", " + y + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(x);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(y);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
				return false;
			if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
				return false;
			return true;
		}

	}

	public class PointCotangentComparator implements Comparator<Point> {
		@Override
		public int compare(Point o1, Point o2) {
			// return (int) (10000 * (o1.cot - o2.cot));
			return (o1.cot > o2.cot) ? 1 : (o1.cot < o2.cot) ? -1 : 0;
		}
	}

	static Logger log = Logger.getLogger(GrahamScan.class.getName());

	/**
	 * Returns a convex hull given an unordered array of points.
	 */
	public List<Point> convexHull(List<Point> data) {
		return findHull(order(data));
	}

	/**
	 * Orders an array of points counterclockwise.
	 */
	public List<Point> order(List<Point> data) {
		// log.info("GrahamScan::order()");
		// first run through all the points and find the upper left [lower left]
		Point p = data.get(0);
		int n = data.size();
		for (int i = 1; i < n; i++) {
			// trace("   p:",p,"d:",data[i]);
			if (data.get(i).y < p.y) {
				// trace("   d.y < p.y / d is new p.");
				p = data.get(i);
			} else if (data.get(i).y == p.y && data.get(i).x < p.x) {
				// trace("   d.y == p.y, d.x < p.x / d is new p.");
				p = data.get(i);
			}
		}
		// log.info("lower left: "+p);

		// next find all the cotangents of the angles made by the point P and
		// the
		// other points
		List<Point> sorted = new ArrayList<Point>();
		// we need arrays for positive and negative values, because Array.sort
		// will put sort the negatives backwards.
		List<Point> pos = new ArrayList<Point>();
		List<Point> neg = new ArrayList<Point>();
		// add points back in order
		for (int i = 0; i < n; i++) {
			double a = data.get(i).x - p.x;
			double b = data.get(i).y - p.y;
			double cot = b / a;
			Point newPoint = new Point(data.get(i).x, data.get(i).y, cot);
			if (newPoint.equals(p)) {
				continue;
			}
			if (cot < 0) {
				neg.add(newPoint);
			} else {
				pos.add(newPoint);
			}
		}
		// sort the arrays
		PointCotangentComparator pcc = new PointCotangentComparator();
		Collections.sort(pos, pcc);
		Collections.sort(neg, pcc);
		// pos.sortOn("cotangent", Array.NUMERIC | Array.DESCENDING);
		// neg.sortOn("cotangent", Array.NUMERIC | Array.DESCENDING);
		// log.info("pos: "+pos);
		// log.info("neg: "+neg);
		sorted.addAll(pos);
		sorted.addAll(neg);
		// log.info("sorted: "+sorted);
		// sorted = neg.addAll(pos);

		// adds upper/lower point to 1st place
		List<Point> ordered = new ArrayList<Point>();
		ordered.add(p);
		for (int i = 0; i < n - 1; i++) {
			// if(p == sorted.get(i)) {
			if (p.equals(sorted.get(i))) {
				continue;
			}
			ordered.add(sorted.get(i));
		}
		return ordered;
	}

	/**
	 * Given an array of points ordered counterclockwise, findHull will filter
	 * the points and return an array containing the vertices of a convex
	 * polygon that envelopes those points.
	 */
	public List<Point> findHull(List<Point> data) {
		// log.info("GrahamScan::findHull()");
		int n = data.size();
		List<Point> hull = new ArrayList<Point>();
		hull.add(data.get(0)); // add the pivot
		hull.add(data.get(1)); // makes first vector

		for (int i = 2; i < n; i++) {
			// log.info("hull size: "+hull.size());
			while (direction(hull.get(hull.size() - 2),
					hull.get(hull.size() - 1), data.get(i)) <= 0) {
				hull.remove(hull.size() - 1);
				// log.info("hull size.b: "+hull.size());
				if (hull.size() < 2) {
					break;
				}
			}
			hull.add(data.get(i));
		}

		return hull;
	}

	public static double direction(Point p1, Point p2, Point p3) {
		// p3 is base point, p2 is new Point
		// > 0 is -right- (left!) turn
		// == 0 is collinear
		// < 0 is -left- (right!) turn
		// we only want -right- (left!) turns, usually we want -right- (left!)
		// turns, but
		// flash's grid is flipped on y.

		// we *want* left turns, so when a right turn is found we remove the
		// preceding point from the hull
		return (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
	}
}
