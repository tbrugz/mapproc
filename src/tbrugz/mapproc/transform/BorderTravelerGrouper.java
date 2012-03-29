package tbrugz.mapproc.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import tbrugz.mapproc.transform.GrahamScan.Point;

//XXX: idea: hibrid border-traveler/convex-hull: convex of each component and then boprder-traveler
public class BorderTravelerGrouper extends PolygonGrouper {

	static Logger log = Logger.getLogger(BorderTravelerGrouper.class.getName());
	
	@Override
	public List<LngLat> getPolygon(List<List<LngLat>> points) {
		int currListIndex = 0; 
		int currLngLatIndex = 0;
		LngLat lowerleft = points.get(currListIndex).get(currLngLatIndex);
		
		for(int i=0;i<points.size();i++) {
			List<LngLat> l = points.get(i);
			if(l.size()==0) { continue; }
			//log.info("i: "+i+"; l: "+l);

			//order points (1st point is lower left)
			List<Point> p = PolygonGrouper.ConvexHullPolygonGrouper.getPoints(l);
			points.set(i, PolygonGrouper.ConvexHullPolygonGrouper.getLngLat( GrahamScan.order(p) ) );
			
			//finds lower left point (starting point)
			LngLat ll = points.get(i).get(0);
			if(ll.lng <= lowerleft.lng && ll.lat < lowerleft.lat ) { currListIndex = i; lowerleft = ll; }
		}

		for(int i=0;i<points.size();i++) {
			List<LngLat> l = points.get(i);
			//log.info("ordered:: i: "+i+"; l: "+l);
		}
		
		List<LngLat> ret = new ArrayList<LngLat>();
		ret.add(lowerleft);
		
		LngLat currLngLat = lowerleft;
		LngLat endpoint = lowerleft;
		int firstLngLatIndexInList = 0;
		
		Set<String> retIds = new HashSet<String>();
		//Set<String> retLists = new HashSet<String>();

		int count = 0;
		do {
			boolean changedList = false;
			if(count==0) {
				//log.warning("init: li:"+currListIndex+", lnglat:"+currLngLatIndex+", "+currLngLat+" count=="+count);
				currLngLat.special = true;
			}

			POINTS_LOOP:
			for(int i=0;i<points.size();i++) {
				if(i==currListIndex) { continue; }
				List<LngLat> l = points.get(i);
				for(int j=0;j<l.size();j++) {
					if(isNearEnough(currLngLat, l.get(j)) 
						//&& ( !ret.contains(l.get(j)) || endpoint.equals(l.get(j)) ) 
						&& ( !retIds.contains(i+"_"+j) ) 
						//&& ( !retLists.contains(String.valueOf(i)) ) 
						) {
						currListIndex = i;
						currLngLatIndex = j;
						currLngLat = l.get(currLngLatIndex);
						
						changedList = true;
						firstLngLatIndexInList = currLngLatIndex;
						currLngLat.special = true;
						//log.warning("changed: li:"+currListIndex+", lnglat:"+currLngLatIndex+", "+currLngLat+" count=="+count);
						break POINTS_LOOP;
					}
				}
			}
			
			//log.info("1.currLngLat: "+currLngLat+" [count="+count+", changed="+changedList+", currListIndex="+currListIndex+", currLngLat="+currLngLatIndex+"]");
			if(! changedList) {
				currLngLatIndex++;
				if(points.get(currListIndex).size()<=currLngLatIndex) {
					if(firstLngLatIndexInList>0) {
						currLngLatIndex = 0; //loop may have initted in the middle of the list
						firstLngLatIndexInList = 0;
					}
					else {
						log.warning("array out of bounds: "+points.get(currListIndex).size()+" / "+currLngLatIndex);
						break;
					}
				}
				currLngLat = points.get(currListIndex).get(currLngLatIndex);
				//ret.add(currLngLat);
			}
			
			count++;
			//if(count>10) break;
			//log.info("currLngLat: "+currLngLat+" [count="+count+", changed="+changedList+", currListIndex="+currListIndex+", currLngLat="+currLngLatIndex+"]");
			
			retIds.add(currListIndex+"_"+currLngLatIndex);
			//retIds.add(String.valueOf(currListIndex));
			ret.add(currLngLat);
		} while(! currLngLat.equals(endpoint));
		
		return ret;
	}
	
	private final static double EPSILON = 0.000001;

	static boolean isNearEnough(LngLat l1, LngLat l2) {
		//log.info("distance: "+l1+", "+l2+" :: "+distance(l1, l2)+" ? "+ (l1 == l2 ? true : distance(l1, l2) < EPSILON));
	    return l1 == l2 ? true : distance(l1, l2) < EPSILON;
	}
	
	static double distance(LngLat l1, LngLat l2) {
		return Math.sqrt( Math.pow(l1.lng - l2.lng, 2) + Math.pow(l1.lat - l2.lat, 2) );
	}

}
