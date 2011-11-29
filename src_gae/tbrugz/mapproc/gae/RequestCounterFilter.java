package tbrugz.mapproc.gae;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tbrugz.mapproc.MapProcServlet;
import tbrugz.mapproc.gae.URLAccessCount.UrlType;

//import static tbrugz.mapproc.MapProcServlet.*;

public class RequestCounterFilter implements Filter {

	static Log log = LogFactory.getLog(RequestCounterFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		log.info("doFilter()");
		
		//chain.doFilter(req, resp);
		
		RequestCountSB rc = new RequestCountSB();
		HttpServletRequest hreq = (HttpServletRequest) req; 
		
		String kmlResource = req.getParameter("kmlResource");
		String csvResource = req.getParameter("csvResource");
		
		//String thisUrl = hreq.getProtocol()+hreq.getServerName()
		//		+(hreq.getLocalPort()!=80?":"+hreq.getLocalPort():"")
		//		+hreq.getRequestURI()

		//XXX: enough for external URLs?
		String thisUrl = hreq.getRequestURI()
				+(hreq.getQueryString()!=null?"?"+hreq.getQueryString():"");

		//TODO: call counter for MAP+SERIES
		
		try {
			chain.doFilter(req, resp);
		}
		catch(IOException e) {
			doCounts(rc, (HttpServletRequest)req, (HttpServletResponse) resp, kmlResource, csvResource, thisUrl, 500);
			throw e;
		}
		catch(ServletException e) {
			doCounts(rc, (HttpServletRequest)req, (HttpServletResponse) resp, kmlResource, csvResource, thisUrl, 500);
			throw e;
		}
		/*catch(Exception e) {
			throw e;
		}*/

		//XXX: new thread for doCounts()?
		doCounts(rc, (HttpServletRequest)req, (HttpServletResponse) resp, kmlResource, csvResource, thisUrl, 200);
		
		log.info("end doFilter()");
	}
	
	void doCounts(RequestCountSB rc, HttpServletRequest req, HttpServletResponse resp, String kmlUrl, String csvUrl, String mapUrl, int status) {
		rc.doCount(UrlType.MAP, kmlUrl, (String) req.getAttribute(MapProcServlet.mapDescription), (Integer)req.getAttribute(MapProcServlet.numOfMapElements), status);
		rc.doCount(UrlType.SERIES, csvUrl, (String) req.getAttribute(MapProcServlet.seriesDescription), (Integer)req.getAttribute(MapProcServlet.numOfSeriesElements), status);
		rc.doCount(UrlType.MAP_SERIES, mapUrl, null, 0, status);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
}
