package tbrugz.xml;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xerces.internal.impl.Constants;

public class SAXSimpleParser extends DefaultHandler {

	static Log log = LogFactory.getLog(SAXSimpleParser.class);
	
	Writer writer;
	
	int level = 0;
	
	public void parseDocument(String file, Writer writer) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(false);  
		String LOAD_EXTERNAL_DTD_FEATURE = Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE;
		
		try {
			spf.setFeature(LOAD_EXTERNAL_DTD_FEATURE, false);
			
			this.writer = writer;

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			log.info("is saxParser validating? " + sp.isValidating());
			
			//parse the file and also register this class for call backs
			sp.parse(file, this);

		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
		
		return;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		log.debug("uri: "+uri+", localName: "+localName+", qName: "+qName+", attributes: "+attributes);
		try {
			writer.write("\n"+getPadding(level));
			writer.write("<"+qName);
			for(int i=0;i<attributes.getLength();i++) {
				writer.write(" "+attributes.getQName(i)+"=\""+attributes.getValue(i)+"\"");
			}
			writer.write(">");
		}
		catch(IOException e) {
			log.warn("IOE: "+e);
		}
		level++;
		//super.startElement(uri, localName, qName, attributes);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		try {
			writer.write(ch, start, length);
		}
		catch (IOException e) {
			log.warn("IOE: "+e);
		}
		//super.characters(ch, start, length);
	}	
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		level--;
		try {
			writer.write("\n"+getPadding(level));
			writer.write("</"+qName+">");
		}
		catch(IOException e) {
			log.warn("IOE: "+e);
		}
	}
	
	String getPadding(int level) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<level;i++) {
			sb.append("  ");
		}
		return sb.toString();
	}

}
