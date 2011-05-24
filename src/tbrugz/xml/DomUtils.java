package tbrugz.xml;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomUtils {

	public static Element getChildByTagName(Element elem, String tagName) {
		NodeList nl = elem.getElementsByTagName(tagName);
		for (int j = 0; j < nl.getLength(); j++) {
			Node nNode2 = nl.item(j);
			if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) nNode2;
			}
		}
		return null;
	}
	
	public static Document getDocumentNodeFromString(String xmlString, DocumentBuilder db) throws SAXException, IOException {
		org.xml.sax.InputSource inStream = new org.xml.sax.InputSource();
		 
	    inStream.setCharacterStream(new java.io.StringReader(xmlString));
	    Document doc = db.parse(inStream);
	    return doc;
	}

}
