package com.cervantesvirtual.metadata;

import junit.framework.TestCase;

public class DCTermTest extends TestCase {

	public void testTerm() throws MetadataException {
	
		DCTerm term1 = DCTerm.term("dc:identifier");
		assertEquals("identifier", term1.getName());
		
		DCTerm term2 = DCTerm.term("dcterm:identifier.slug");
		assertEquals("identifier.slug", term2.getName());
	}

}