package tbrugz.mapproc.gae;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class URLAccessCount {
	
	//TODO: add name, description (may be shown in "accessed list")
	
	public enum UrlType {
		MAP, SERIES, MAP_SERIES;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	String url;
	UrlType type; //MAP, SERIES, MAP+SERIES
	//String type;
	
	long counter;
}
