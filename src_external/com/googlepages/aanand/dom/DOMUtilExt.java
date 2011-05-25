package com.googlepages.aanand.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sun.org.apache.xerces.internal.util.DOMUtil;

/**
 * 
 * see: http://programmatica.blogspot.com/2006/12/sorting-xml-in-java.html
 *
 */
public class DOMUtilExt extends DOMUtil {
	
	public static class IdAttribComparator extends AttribComparator {
		public IdAttribComparator() {
			super("id");
		}
	}

	public static class AttribComparator implements Comparator {
		
		String attribName = "";
		
		public AttribComparator(String attribName) {
			this.attribName = attribName;
		}
		
		public int compare(Object arg0, Object arg1) {

			if (arg0 instanceof Element && arg1 instanceof Element) {
				return ((Element) arg0).getAttribute(attribName).compareTo(
						((Element) arg1).getAttribute(attribName));
			} else {
				return ((Node) arg0).getNodeName().compareTo(
						((Node) arg1).getNodeName());
			}

		}

	}

        /**
         * Sorts the children of the given node upto the specified depth if
         * available
         * 
         * @param node -
         *            node whose children will be sorted
         * @param descending -
         *            true for sorting in descending order
         * @param depth -
         *            depth upto which to sort in DOM
         * @param comparator -
         *           comparator used to sort, if null a default NodeName
         *           comparator is used.
         */
        public static void sortChildNodes(Node node, boolean descending,
                        int depth, Comparator comparator) {

                List nodes = new ArrayList();
                NodeList childNodeList = node.getChildNodes();
                if (depth > 0 && childNodeList.getLength() > 0) {
                   for (int i = 0; i < childNodeList.getLength(); i++) {
                        Node tNode = childNodeList.item(i);
                        sortChildNodes(tNode, descending, depth - 1,
                                       comparator);
                           // Remove empty text nodes
                        if ((!(tNode instanceof Text))
                                || (tNode instanceof Text && ((Text) tNode)
                                        .getTextContent().trim().length() > 1))
                        {    
                              nodes.add(tNode);
                        }
                   }
                   Comparator comp = (comparator != null) ? comparator
                                : new DefaultNodeNameComparator();
                   if (descending)
                   {
                    //if descending is true, get the reverse ordered comparator
                        Collections.sort(nodes, Collections.reverseOrder(comp));
                   } else {
                        Collections.sort(nodes, comp);
                   }

                  for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                        Node element = (Node) iter.next();
                        node.appendChild(element);
                  }
                }

        }

}

class DefaultNodeNameComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
                return ((Node) arg0).getNodeName().compareTo(
                                ((Node) arg1).getNodeName());
        }

}
