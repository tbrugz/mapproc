package tbrugz.mapproc.gae;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class URLAccessCount {
	
	//TODO: add name, description (may be shown in "accessed list")
	
	public enum UrlType {
		MAP, SERIES, MAP_SERIES;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	String url;
	UrlType type; //MAP, SERIES, MAP+SERIES
	//String type;
	
	Integer counter = 0;
	
	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public UrlType getType() {
		return type;
	}

	public void setType(UrlType type) {
		this.type = type;
	}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}

	@Override
	public String toString() {
		return id+":[c="+counter+"]:"+url;
	}
	
}
