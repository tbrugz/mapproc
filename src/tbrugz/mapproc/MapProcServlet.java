package tbrugz.mapproc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tbrugz.stats.StatsUtils.ScaleType;

public class MapProcServlet extends HttpServlet {
	
	public static final String KML_MIMETYPE = "application/vnd.google-earth.kml+xml";
	public static final String TXT_MIMETYPE = "text/plain";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String kmlUrl = req.getParameter("kmlUrl");
		String csvUrl = req.getParameter("csvUrl");
		String categoriesUrl = req.getParameter("categoriesUrl");

		String kmlWebPath = req.getParameter("kmlPath");
		String csvWebPath = req.getParameter("csvPath");
		String categoriesPath = req.getParameter("categoriesPath");
		
		//categories params
		String colorFrom = req.getParameter("colorFrom");
		String colorTo = req.getParameter("colorTo");
		
		//String kmlURI = "work/input/Municipalities_of_RS.kml";
		
		try {
			MapProc lm = new MapProc();
			/*File csvIn = null;
			try {
				csvIn = new File(new URI(csvUrl));
			}
			catch(IllegalArgumentException e) {
				csvIn = new File(csvUrl);
			}
			catch(URISyntaxException e) {
				csvIn = new File(csvUrl);
			}
			FileReader seriesFile = new FileReader(csvIn);*/
			InputStreamReader seriesReader = new InputStreamReader(new URL(csvUrl).openStream());
			resp.setContentType(KML_MIMETYPE);
			resp.setHeader("Content-Disposition","attachment; filename=mapproc.kml");
			//if(1==1) throw new SAXException("bla bla");
			if(categoriesUrl!=null) {
				BufferedReader catsReader = new BufferedReader(new InputStreamReader(new URL(categoriesUrl).openStream()));
				lm.doIt(kmlUrl, MapProc.getIndexedSeries(seriesReader), resp.getWriter(), catsReader, colorFrom, colorTo);
			}
			else {
				int numOfCategories = Integer.parseInt( req.getParameter("numOfCategories") );
				ScaleType scaleType = ScaleType.valueOf( req.getParameter("scaleType") );
				lm.doIt(kmlUrl, MapProc.getIndexedSeries(seriesReader), resp.getWriter(), scaleType, numOfCategories, colorFrom, colorTo);
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
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
