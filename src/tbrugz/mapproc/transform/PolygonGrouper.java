package tbrugz.mapproc.transform;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<LngLat> getFlatList(List<List<LngLat>> points) {
		List<LngLat> flatlist = new ArrayList<LngLat>();
		for(List<LngLat> l: points) {
			flatlist.addAll(l);
		}
		return flatlist;
	}
}
