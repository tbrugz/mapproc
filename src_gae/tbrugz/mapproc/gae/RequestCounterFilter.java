package tbrugz.mapproc.gae;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tbrugz.mapproc.gae.URLAccessCount.UrlType;

public class RequestCounterFilter implements Filter {

	static Log log = LogFactory.getLog(RequestCounterFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		log.info("doFilter()");
		
		chain.doFilter(req, resp);
		
		RequestCountSB rc = new RequestCountSB();
		
		String kmlResource = req.getParameter("kmlResource");
		rc.doCount(UrlType.MAP, kmlResource);
		
		String csvResource = req.getParameter("csvResource");
		rc.doCount(UrlType.SERIES, csvResource);

		//TODO: call counter for MAP+SERIES
		
		log.info("end doFilter()");
		/*try {
			chain.doFilter(req, resp);
		}
		catch(IOException e) {
			throw e;
		}
		catch(ServletException e) {
			throw e;
		}*/
		/*catch(Exception e) {
			throw e;
		}*/
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
}
