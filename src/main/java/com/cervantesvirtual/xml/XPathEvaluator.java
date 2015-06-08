package com.cervantesvirtual.xml;

import org.w3c.dom.*;
import javax.xml.xpath.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.namespace.*;
import java.io.File;

public class XPathEvaluator {
    DocumentBuilder builder;
    XPathExpression exp;

    /**
     * Auxiliary class required to process namespaces (needs improvement)
     */
    public static class SimpleNamespaceContext implements NamespaceContext {
        @Override
        public String getNamespaceURI(String prefix) {
            if (prefix.equals("marc"))
                return "http://www.loc.gov/MARC21/slim";
            else
                return null;//XMLConstants.NULL_NS_URI;
        }
        
        @Override
        public String getPrefix(String namespace) {
            if (namespace.equals("http://www.loc.gov/MARC21/slim")) {
		return "marc";
	    } else {
                return null;
	    }
	}

        @Override
        public java.util.Iterator getPrefixes(String namespace) {
            return null;
        }
    }  
    
    public XPathEvaluator (String xpathExpression) 
	throws Exception {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	XPath xpath = XPathFactory.newInstance().newXPath();
	factory.setNamespaceAware(true);   // Don't forget!
	builder = factory.newDocumentBuilder();
	xpath.setNamespaceContext(new  SimpleNamespaceContext());
	exp = xpath.compile(xpathExpression);
    }

    public int evaluate(File file) throws Exception {
	Document doc = builder.parse(file);
	NodeList nodes = (NodeList) 
	    exp.evaluate(doc, XPathConstants.NODESET);		  
	int length = nodes.getLength();

	if (length > 0) {
	    System.err.println(file);
	}
		  
	for (int n = 0 ; n < length ; n++) {
	    Node node = nodes.item(n);
	    String val = node.getNodeName() + 
		" : " + node.getTextContent();
		      // getTextContent().//.replaceAll("\\s+", " "));
		      System.out.println(val);
	}
	return length;
    }
    

    /**
     * The main code
     * @throws java.lang.Exception
     */
    public static void main (String[] args) throws Exception {
	if (args.length < 2) {
	    System.err.println("Args: xpression file1 file2...");
	} else {
	    XPathEvaluator evaluator = new XPathEvaluator(args[0]);
	    int totalMatches = 0;
     	    for (int k = 1; k < args.length; ++k) {
		File file = new File(args[k]);
		if (file.isDirectory()) {
		    for (File subfile : file.listFiles()) {
			if (subfile.getName().endsWith(".xml")) {
			    totalMatches += evaluator.evaluate(subfile);
			}
		    }
		} else if (args[k].endsWith(".xml")) {
		    totalMatches += evaluator.evaluate(file);
		}

	    }
	    System.err.printf("%d node(s) matched %s.%n", 
			      totalMatches, args[0]);
	}
    }
    
}
