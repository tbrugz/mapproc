package tbrugz.mapproc.transform;

import java.util.ArrayList;
import java.util.List;

import tbrugz.mapproc.transform.GrahamScan.Point;
import tbrugz.stats.StatsUtils;

public abstract class PolygonGrouper {

	public abstract List<LngLat> getPolygon(List<List<LngLat>> points);	
	
	public static class LatLongMinMaxPolygonGrouper extends PolygonGrouper {
		@Override
		public List<LngLat> getPolygon(List<List<LngLat>> points) {
			return getPolygonSimple(PolygonGrouper.getFlatList(points));
		}
		static List<LngLat> getPolygonSimple(List<LngLat> points) {
			//TODO: get max/min lat/long - then go clockwise
			//InitialVersion: just get the 4 extreme points
			double[] lat = new double[points.size()];
			double[] lng = new double[points.size()];
			List<LngLat> ret = new ArrayList<LngLat>();
			
			for(int i=0;i<points.size();i++) {
				LngLat ll = points.get(i);
				lat[i] = ll.lat;
				lng[i] = ll.lng;
			}

			//clockwise on north-east
			ret.add(points.get(StatsUtils.maxIndex(lat)));
			ret.add(points.get(StatsUtils.maxIndex(lng)));
			ret.add(points.get(StatsUtils.minIndex(lat)));
			ret.add(points.get(StatsUtils.minIndex(lng)));
			
			return ret;
		}
	}
	
	public static class ConvexHullPolygonGrouper extends PolygonGrouper {
		@Override
		public List<LngLat> getPolygon(List<List<LngLat>> points) {
			//GrahamScan gs = new GrahamScan();
			//return getLngLat( gs.order( getPoints(points) ) ); //XXX: should use convexHull() 
			return getLngLat( GrahamScan.convexHull( getPoints( getFlatList( points) ) ) );
		}
		
		public static List<Point> getPoints(List<LngLat> points) {
			List<Point> ret = new ArrayList<GrahamScan.Point>();
			for(LngLat ll: points) {
				ret.add(new Point(ll.lng, ll.lat, 0));
			}
			return ret;
		}

		public static List<LngLat> getLngLat(List<Point> points) {
			List<LngLat> ret = new ArrayList<LngLat>();
			for(Point p: points) {
				ret.add(new LngLat(p.x, p.y));
			}
			return ret;
		}
	}
	
	public static <T extends Object> List<T> getFlatList(List<List<T>> points) {
		List<T> flatlist = new ArrayList<T>();
		for(List<T> l: points) {
			flatlist.addAll(l);
		}
		return flatlist;
	}
}
