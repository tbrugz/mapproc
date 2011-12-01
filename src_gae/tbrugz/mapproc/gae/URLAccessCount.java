package tbrugz.mapproc.gae;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class URLAccessCount {
	
	//TODOne: add name, description (may be shown in "accessed list")
	
	public enum UrlType {
		MAP, SERIES, MAP_SERIES;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	String url;
	UrlType type; //MAP, SERIES, MAP+SERIES
	
	Integer counter = 0;
	
	//XXXxx: add httpStatus (of latest request)?
	Integer httpStatus;
	String description;
	Date lastAccess;
	Integer numOfElements;
	
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
	
	public Integer getHttpStatus() {
		return httpStatus!=null?httpStatus:200;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	public Integer getNumOfElements() {
		return numOfElements!=null?numOfElements:0;
	}

	public void setNumOfElements(Integer numOfElements) {
		this.numOfElements = numOfElements;
	}

	@Override
	public String toString() {
		return id+":[c="+counter+"]:"+url;
	}
	
}
