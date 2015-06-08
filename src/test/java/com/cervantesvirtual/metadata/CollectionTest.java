package com.cervantesvirtual.metadata;

import junit.framework.TestCase;
import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.MetadataFormat;
import org.w3c.dom.Document;
import java.io.*;

public class CollectionTest extends TestCase {
	public void testCollection() {
            /*
		try {
			java.net.URL urlin = this.getClass().getResource("/MARCRecord.xml");
			File in = new File(urlin.getFile());	
				System.err.println(in);
			java.net.URL urlout = this.getClass()
					.getResource("/MARCRecord.txt");
			File out = new File(urlout.getFile());
			Document doc = 
				com.cervantesvirtual.metadata.util.DocumentParser.parse(in);
			Collection cin = new Collection(MetadataFormat.MARC, doc);

			int size = cin.size();
			assertEquals(size, 1);
			OutputStream os = new FileOutputStream(out);
			// System.err.println("READ");
			cin.write(os, "UTF-8");
			// System.out.println("WRITE");
			Collection cout = new Collection(MetadataFormat.MARC,
					new FileInputStream(out));
			assertTrue(cin.equals(cout));
		} catch (Exception e) {
			e.printStackTrace();
		}
             * */
             
	}

	public static void main(String[] args) {
		new CollectionTest().testCollection();
	}
}