package tbrugz.mapproc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
	static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
	
	public static String stringSnippet(String s, int maxlen) {
		if(s==null) { return null; }
		if(s.length()>maxlen && maxlen>3) {
			return s.substring(0, maxlen-3)+"...";
		}
		return s;
	}
	
	public static String getDateString(Date d) {
		if(d==null) { return null; }
		return df.format(d);
	}
}
