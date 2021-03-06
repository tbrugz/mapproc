package tbrugz.mapproc.transform;

public class LngLat {
	double lng, lat;
	boolean special = false;
	
	public LngLat() {
	}
	
	public LngLat(double lng, double lat) {
		this.lng = lng; this.lat = lat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
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
		LngLat other = (LngLat) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "["+lng+", "+lat+"]";
	}
	
}
