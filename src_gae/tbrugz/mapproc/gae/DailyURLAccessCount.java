package tbrugz.mapproc.gae;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class DailyURLAccessCount extends URLAccessCount {
	
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		return getId()+":[c="+counter+";d="+date+"]:"+url;
	}
	
}
