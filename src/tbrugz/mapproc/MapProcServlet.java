package tbrugz.mapproc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tbrugz.stats.StatsUtils.ScaleType;

/*
 * TODO: do not return map if map contains no placemark (apart from categories labels)
 */
public class MapProcServlet extends HttpServlet {
	
	public static final String KML_MIMETYPE = "application/vnd.google-earth.kml+xml";
	public static final String XML_MIMETYPE = "text/xml";
	public static final String TXT_MIMETYPE = "text/plain";
	
	boolean kmlUrlAllowed = true;
	boolean csvUrlAllowed = true;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String kmlUrl = req.getParameter("kmlUrl");
		String csvUrl = req.getParameter("csvUrl");
		String categoriesUrl = req.getParameter("categoriesUrl");

		String kmlResource = req.getParameter("kmlResource");
		String csvResource = req.getParameter("csvResource");
		String categoriesResource = req.getParameter("categoriesResource");
		
		String removeIfNotFoundStr = req.getParameter("removeIfNotFound");
		boolean removeIfNotFound = removeIfNotFoundStr!=null && !removeIfNotFoundStr.equals("");
		
		String genCatLimitsFromExistingPlacemarksStr = req.getParameter("genCatLimitsFromExistingPlacemarks");
		boolean genCatLimitsFromExistingPlacemarks = genCatLimitsFromExistingPlacemarksStr!=null && !genCatLimitsFromExistingPlacemarksStr.equals("");

		String mime = req.getParameter("mime");
		
		//categories params
		String colorFrom = req.getParameter("colorFrom");
		String colorTo = req.getParameter("colorTo");
		
		try {
			MapProc lm = new MapProc();

			InputStream kmlStream = null;
			InputStreamReader seriesReader = null;
			
			kmlStream = getStream(kmlResource, kmlUrlAllowed, kmlUrl, "KML");
			/*if(kmlResource!=null) {
				kmlStream = getServletContext().getResourceAsStream(kmlResource);
			}
			else if(kmlUrlAllowed){
				kmlStream = new URL(kmlUrl).openStream();
			}
			else {
				throw new RuntimeException("no KML defined");
			}*/
			
			seriesReader = new InputStreamReader(getStream(csvResource, csvUrlAllowed, csvUrl, "CSV Data"));
			/*if(csvResource!=null) {
				seriesReader = new InputStreamReader(getServletContext().getResourceAsStream(csvResource));
			}
			else if(csvUrlAllowed){
				seriesReader = new InputStreamReader(new URL(csvUrl).openStream());
			}
			else {
				throw new RuntimeException("no CSV Data defined");
			}*/
			
			if(mime==null) {
				resp.setContentType(KML_MIMETYPE);
				resp.setHeader("Content-Disposition","attachment; filename=mapproc.kml");
			}
			else {
				resp.setContentType(XML_MIMETYPE);
			}
			
			if((categoriesResource!=null && !categoriesResource.equals("")) || (categoriesUrl!=null && categoriesUrl.equals(""))) {
				//BufferedReader catsReader = new BufferedReader(new InputStreamReader(new URL(categoriesUrl).openStream()));
				BufferedReader catsReader = new BufferedReader(new InputStreamReader(getStream(categoriesResource, csvUrlAllowed, categoriesUrl, "CSV Categories")));
				
				lm.doIt(kmlStream, MapProc.getIndexedSeries(seriesReader), resp.getWriter(), catsReader, colorFrom, colorTo, removeIfNotFound);
			}
			else {
				int numOfCategories = Integer.parseInt( req.getParameter("numOfCategories") );
				ScaleType scaleType = ScaleType.valueOf( req.getParameter("scaleType") );
				lm.doIt(kmlStream, MapProc.getIndexedSeries(seriesReader), resp.getWriter(), scaleType, numOfCategories, colorFrom, colorTo, removeIfNotFound, genCatLimitsFromExistingPlacemarks);
			}
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
			//resp.setContentType(TXT_MIMETYPE);
			//resp.setHeader("Content-Disposition","inline");
			//e.printStackTrace(resp.getWriter());
		} catch (SAXException e) {
			throw new RuntimeException(e);
			//e.printStackTrace(resp.getWriter());
		}
	}
	
	InputStream getStream(String resourcePath, boolean allowUrl, String url, String contentType) throws MalformedURLException, IOException {
		if(resourcePath!=null) {
			return getServletContext().getResourceAsStream(resourcePath);
		}
		else if(allowUrl){
			return new URL(url).openStream();
		}
		else {
			throw new RuntimeException("no "+contentType+" defined");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
