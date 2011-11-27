package tbrugz.mapproc.gae;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tbrugz.mapproc.gae.URLAccessCount.UrlType;

//SB: session bean
public class RequestCountSB {

	static Log log = LogFactory.getLog(RequestCountSB.class);
	
	EntityManager em;
	
	void doCount(UrlType type, String url) {
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
			URLAccessCount uac2 = em.merge(uac);
			log.info("merged uac: "+uac2);
		}
		//t.begin();
		//em.flush();
		//em.persist(uac);
		t.commit();
		em.close();
		log.info("persisted: "+uac);
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
