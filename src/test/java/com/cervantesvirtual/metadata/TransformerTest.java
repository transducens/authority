package com.cervantesvirtual.metadata;

import java.io.File;
import java.net.URL;
import junit.framework.TestCase;
import org.w3c.dom.Document;

public class TransformerTest extends TestCase {

    public void testTransformer() throws Exception {
        URL urlin = this.getClass().getResource("/MARCRecord.xml");
        System.out.println(urlin);
        File in = new File(urlin.toURI());
		//File out = new File("DCRecord.txt"); 

        Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(in);
        Collection cin = new Collection(MetadataFormat.MARC, doc);
        //cin.writeXML(System.out);
        for (Record source : cin.getRecords()) {
            Record target = Transformer.transform(source);
            System.out.println(target.toXML());
        }
    }
}
