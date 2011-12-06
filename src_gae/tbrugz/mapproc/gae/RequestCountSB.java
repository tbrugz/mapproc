package tbrugz.mapproc.gae;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tbrugz.mapproc.gae.URLAccessCount.URLAccessCountComparator;
import tbrugz.mapproc.gae.URLAccessCount.UrlType;

/*
 * SB: session bean
 * 
 * abous GAE transactions:
 * http://code.google.com/intl/en/appengine/docs/java/datastore/transactions.html#Disabling_Transactions_and_Porting_Existing_JDO_Apps
 */
public class RequestCountSB {

	static Log log = LogFactory.getLog(RequestCountSB.class);
	
	EntityManager em;
	
	public void doCount(UrlType type, String url, String desc, int numOfElements, int httpStatus) {
		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction t = em.getTransaction();
		t.begin();
		
		URLAccessCount uac = null;
		
		Query q = em.createQuery("select from " + URLAccessCount.class.getName()
				+" where url = :url");
				//+" and type = "+type);
		q.setParameter("url", url);
		
		List lo = q.getResultList();
		//javax.persistence.NoResultException
		//Object o = q.getSingleResult();
		//if(o==null) {
		if(lo==null || lo.size()==0) {
			log.info("new UAC");
			uac = new URLAccessCount();
			uac.url = url;
			uac.type = type;
			uac.counter = 1;
			updateInfo(uac, desc, numOfElements, httpStatus);
			em.persist(uac);
		}
		else {
			//uac = (URLAccessCount) o;
			uac = (URLAccessCount) lo.get(0);
			log.info("existing UAC: count: "+uac.counter);
			uac.setCounter(uac.getCounter()+1);
			//uac.counter++;
			//boolean contains = em.contains(uac);
			//log.info("existing UAC new value: count: "+uac.counter+" ; contains: "+contains);
			updateInfo(uac, desc, numOfElements, httpStatus);
			em.merge(uac);
			//log.info("merged uac: "+uac2);
		}
		
		t.commit();
		doDailyCount(em, type, url, desc, numOfElements, httpStatus);
		
		//t.begin();
		//em.flush();
		//em.persist(uac);
		em.close();
		log.info("persisted: "+uac);
	}
	
	private void doDailyCount(EntityManager em, UrlType type, String url, String desc, int numOfElements, int httpStatus) {
		DailyURLAccessCount uac = null;
		Date now = new Date(); //use now? will query get it?
		Date today = getToday();

		EntityTransaction t = em.getTransaction();
		t.begin();
		
		Query q = em.createQuery("select from " + DailyURLAccessCount.class.getName()
				+" where url = :url and date = :date");
				//+" and type = "+type);
		q.setParameter("url", url);
		q.setParameter("date", today);
		
		List lo = q.getResultList();
		//javax.persistence.NoResultException
		//Object o = q.getSingleResult();
		//if(o==null) {
		if(lo==null || lo.size()==0) {
			log.info("new DUAC");
			uac = new DailyURLAccessCount();
			uac.url = url;
			uac.type = type;
			uac.counter = 1;
			uac.setDate(today);
			updateInfo(uac, desc, numOfElements, httpStatus);
			em.persist(uac);
		}
		else {
			//uac = (URLAccessCount) o;
			uac = (DailyURLAccessCount) lo.get(0);
			log.info("existing DUAC: count: "+uac.counter);
			uac.setCounter(uac.getCounter()+1);
			//uac.counter++;
			//boolean contains = em.contains(uac);
			//log.info("existing UAC new value: count: "+uac.counter+" ; contains: "+contains);
			updateInfo(uac, desc, numOfElements, httpStatus);
			em.merge(uac);
			//log.info("merged uac: "+uac2);
		}
		
		t.commit();
	}
	
	static Date getToday() {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();       // get calendar instance
		cal.setTime(now);                            // set cal to date
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millis in second
		Date zeroedDate = cal.getTime();             // actually computes the new Date
		return zeroedDate;
	}
	
	private void updateInfo(URLAccessCount uac, String desc, int numOfElements, int httpStatus) {
		uac.setDescription(desc);
		uac.setNumOfElements(numOfElements);
		uac.setHttpStatus(httpStatus);
		uac.setLastAccess(new Date());
	}
	
	public List getMostViewed(UrlType type) {
		if(em!=null) { closeEM(); }
		em = EMF.get().createEntityManager();
		
		Query q = em.createQuery("select from " + URLAccessCount.class.getName()
				+" where type = :type"
				+" order by counter desc"
				);
		q.setParameter("type", type);
		return q.getResultList();
	}
	
	public List getMostViewedLastXDays(UrlType type, int days) {
		if(em!=null) { closeEM(); }
		em = EMF.get().createEntityManager();
		//Date today = getToday();
		Calendar c = Calendar.getInstance();
		c.setTime(getToday());
		c.add(Calendar.DAY_OF_MONTH, -days);
		Date xDaysBefore = c.getTime();
		
		Query q = em.createQuery("select from " + DailyURLAccessCount.class.getName()
				+" where type = :type"
				+" and date > :date"
				+" order by date, counter desc"
				);
		q.setParameter("type", type);
		q.setParameter("date", xDaysBefore);
		return q.getResultList();
	}
	
	public List<URLAccessCount> groupByURL(List<URLAccessCount> lo) {
		Map<String, URLAccessCount> map = new HashMap<String, URLAccessCount>();
		for(URLAccessCount u: lo) {
			URLAccessCount mu = map.get(u.url);
			if(mu==null) {
				mu = u;
				map.put(mu.url, mu);
			}
			else {
				//aggregate function: SUM ;)
				mu.setCounter(mu.getCounter()+u.getCounter());
				//XXX: update other attributes? only if newer...
			}
		}
		
		List<URLAccessCount> retlo = new ArrayList<URLAccessCount>();
		retlo.addAll(map.values());
		Collections.sort(retlo, Collections.reverseOrder(URLAccessCountComparator.getIt())); //reverseOrder: desc
		return retlo;
	}

	public void closeEM() {
		if(em==null) {
			log.warn("EM is null");
			return;
		}
		if(em.isOpen()) {
			em.close();
		}
	}

}
