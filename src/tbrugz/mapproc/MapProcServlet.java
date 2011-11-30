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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import tbrugz.stats.StatsUtils.ScaleType;
import tbrugz.xml.XmlPrinter;

/*
 * TODO: do not return map if map contains no placemark (apart from categories labels)
 */
public class MapProcServlet extends HttpServlet {
	
	public static final String KML_MIMETYPE = "application/vnd.google-earth.kml+xml";
	public static final String XML_MIMETYPE = "text/xml";
	public static final String TXT_MIMETYPE = "text/plain";

	public static final String numOfMapElements = "numOfMapElements";
	public static final String numOfSeriesElements = "numOfSeriesElements";
	public static final String mapDescription = "mapDescription";
	public static final String seriesDescription = "seriesDescription";
	
	public static final String PARAM_KML = "kml";
	public static final String PARAM_CSV = "csv";
	public static final String PARAM_CAT = "cat";
	
	boolean kmlUrlAllowed = true;
	boolean csvUrlAllowed = true;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String kml = req.getParameter(PARAM_KML);
		String csv = req.getParameter(PARAM_CSV);
		String cat = req.getParameter(PARAM_CAT);

		//deprecated params
		if(kml==null) {
			kml = req.getParameter("kmlResource");
			if(kml==null) kml = req.getParameter("kmlUrl");
			if(kml==null) throw new RuntimeException("parameter 'kml' undefined");
		}
		if(csv==null) {
			csv = req.getParameter("csvResource");
			if(csv==null) csv = req.getParameter("csvUrl");
			if(csv==null) throw new RuntimeException("parameter 'csv' undefined");
		}
		if(cat==null) {
			cat = req.getParameter("categoriesResource");
			if(cat==null) cat = req.getParameter("categoriesUrl");
		}
		// /deprecated
		
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
			
			kmlStream = getStream(kml, kmlUrlAllowed, "KML");
			/*if(kmlResource!=null) {
				kmlStream = getServletContext().getResourceAsStream(kmlResource);
			}
			else if(kmlUrlAllowed){
				kmlStream = new URL(kmlUrl).openStream();
			}
			else {
				throw new RuntimeException("no KML defined");
			}*/
			
			seriesReader = new InputStreamReader(getStream(csv, csvUrlAllowed, "CSV Data"));
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

			Document doc = null;
			if(cat!=null && !cat.equals("")) {
				//BufferedReader catsReader = new BufferedReader(new InputStreamReader(new URL(categoriesUrl).openStream()));
				BufferedReader catsReader = new BufferedReader(new InputStreamReader(getStream(cat, csvUrlAllowed, "CSV Categories")));
				
				doc = lm.doIt(kmlStream, MapProc.getIndexedSeries(seriesReader), catsReader, colorFrom, colorTo, removeIfNotFound);
			}
			else {
				int numOfCategories = Integer.parseInt( req.getParameter("numOfCategories") );
				ScaleType scaleType = ScaleType.valueOf( req.getParameter("scaleType") );
				doc = lm.doIt(kmlStream, MapProc.getIndexedSeries(seriesReader), scaleType, numOfCategories, colorFrom, colorTo, removeIfNotFound, genCatLimitsFromExistingPlacemarks);
			}

			req.setAttribute(numOfMapElements, lm.numOfMapElements);
			req.setAttribute(numOfSeriesElements, lm.numOfSeriesElements);
			req.setAttribute(mapDescription, lm.mapDescription);
			req.setAttribute(seriesDescription, lm.seriesDescription);
			
			XmlPrinter.serialize(doc, resp.getWriter());
		} catch (ParserConfigurationException e) {
			//resp.setContentType(TXT_MIMETYPE);
			//resp.setHeader("Content-Disposition","inline");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	InputStream getStream(String path, boolean allowUrl, String contentType) throws MalformedURLException, IOException {
		if(path==null || path.equals("")) {
			throw new RuntimeException("no "+contentType+" param defined");
		}
		
		if(path.startsWith("/")) {
			InputStream is = getServletContext().getResourceAsStream(path);
			if(is==null) {
				throw new RuntimeException(contentType+" resource not found");
			}
			return is;
		}
		else if(allowUrl){
			//XXX: test for "http[s]://" ?
			if(path.startsWith("http://") || path.startsWith("https://")) {
				return new URL(path).openStream();
			}
			else {
				throw new RuntimeException("malformed URL for "+contentType);
			}
		}
		else {
			throw new RuntimeException("malformed param "+contentType);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
