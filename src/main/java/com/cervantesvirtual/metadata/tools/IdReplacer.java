package com.cervantesvirtual.metadata.tools;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

import org.w3c.dom.Document; 
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

public class IdReplacer {
    static javax.xml.parsers.DocumentBuilder docBuilder;
    static javax.xml.transform.Transformer transformer;
    static Runtime runtime; 

    Map<String, String> preferred;

    static {
	try {
	    docBuilder =   javax.xml.parsers.DocumentBuilderFactory
		.newInstance()
		.newDocumentBuilder();
	    transformer = javax.xml.transform.TransformerFactory
		.newInstance().newTransformer();
	} catch (Exception e) {
	    System.err.println("Could not create document builder and transformer");
	}
	runtime = Runtime.getRuntime();
    }
    
    public IdReplacer () 
	throws IOException {
	BufferedReader reader = 
	    new BufferedReader(new InputStreamReader(System.in));   
	String ref = null;

	preferred = new HashMap<String, String>();
	while (reader.ready()) {
	    String line = reader.readLine().trim();
	    if (line.length() > 0) {
		String[] tokens = line.split("\\p{Space}+");
	   
		if (tokens.length > 1 && tokens[1].equals("$0")) {
		    String tag = new String(tokens[0]);
		    String id = new String(tokens[2]);
		    if (tag.startsWith("1")) {
			ref = id; 
		    } else if (!ref.equals(id)) {
		    preferred.put(id, ref);
		    }
		} else {
		    System.err.println("Wrong record " + line);
		}
	    }
	}
	System.err.println(preferred.size() + " replacement rules");
    }

    /**
     * @param nodes a list of nodes.
     * @param attName the name of an attribute.
     * @param attValue the required value of the attribute. 
     * @return nodes in the list with an a attribute with name attName and value attValue.
     */
    private List<Node> select (NodeList nodes,  String attName, String attValue) {
	List<Node> result = new ArrayList<Node>();
	for (int k = 0; k < nodes.getLength(); ++k) {
	    Node node = nodes.item(k);
	    String value = node.getAttributes()
		.getNamedItem(attName)
		.getNodeValue();
	    if (attValue.equals(value)) {
		result.add(node); 
	    }
	} 
	return result;
    }

    /**
     * @param nodes a list of nodes
     * @param attName the name of an attribute.
     * @param attValues an array of valid attribute values.
     * @return nodes in the list with an a attribute with name attName and value attValue.
     */
    private List<Node> select (NodeList nodes,  
			       String attName, String[] attValues) {
	List<Node> result = new ArrayList<Node>();
	for (String attValue : attValues) {
	    result.addAll(select(nodes, attName, attValue));
	}
	return result;
    }

    public void replace (File file, PrintStream out) 
	throws org.xml.sax.SAXException, IOException {
	Document doc = docBuilder.parse(file);	
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new StringWriter());
	String[] tags = {"100", "110", "111", "700", "710", "711"};
	List<Node> nodes = 
	    select(doc.getElementsByTagName("marc:datafield"), "tag", tags);

	for (Node node : nodes) {
	    List<Node> children = select(node.getChildNodes(), "code", "0");
	    for (Node child : children) {
		String id = child.getTextContent().trim();  
		if (preferred.containsKey(id)) {
		    String replacement = preferred.get(id);
		    child.setTextContent(replacement);
		    System.err.println(file + ":" +
				       id + "->" + replacement);
		}
	    }
	}

	// Write result
	try {
	    transformer.transform(source, result);
	    out.print(result.getWriter().toString());
	} catch (Exception e) {
	    System.err.println("Could not transform replaced file " + file + " to XML"); 
	}
    }

    /**
     * @param file the file to be replaced.
     * @param dir the directory where the new file must be created or
     * null if the output will be dumped to stdout.
     */
    public void replace (File in, File outdir) {
	try {
	    PrintStream out =  (outdir == null) ?
		System.out :
		new PrintStream(outdir.getAbsolutePath() + "/" + in.getName()); 
	    replace(in, out);
	} catch (FileNotFoundException e) {
	    System.err.println("Could not write to " + outdir 
			       + "/" + in.getName() );
	} catch (org.xml.sax.SAXException e) {
	    System.err.println("Error parsing file " + in);
	} catch (IOException e) {
	    System.err.println("Error parsing file " + in);
	}
    }

    public static void main (String[] args) 
	throws Exception {
	IdReplacer replacer = new IdReplacer();
	File outdir = null;
	int numFiles = 0; 
	
	for (int n = 0; n < args.length; ++n) {
	    String arg = args[n];
	    if (arg.equals("-o")) {
		outdir = new File(args[++n]);
	    } else {
		File file = new File(arg);
		if (outdir != null &&
		    file.getAbsolutePath().equals(outdir.getAbsolutePath())) {
		    System.err.println("Output dir cannot be input dir:"+
				       "no output for file" + file);
		}
		if (file.isDirectory()) {
		    for (File fileInDir : file.listFiles()) {
			if (fileInDir.getName().endsWith(".xml")) {
			    replacer.replace(fileInDir, outdir);
			    ++numFiles;
			    //System.err.println(numFiles);
			    if (numFiles % 10000 == 0) {
				runtime.gc();  // some files must be closed
			    }
			}
		    }
		} else if (file.getName().endsWith(".xml")) {
		    replacer.replace(file, outdir);
		    ++numFiles;
		}
	    }
	}
	System.err.println("Replaced " + numFiles + " files");
    }

}