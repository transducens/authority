package com.cervantesvirtual.metadata;

import java.io.IOException;
import java.io.Reader;

import org.w3c.dom.Node;


public class DCRecord extends Record {

	public DCRecord(Node record) throws MetadataException {
		super(MetadataFormat.DC, record);
	}

	public DCRecord(Reader text) throws IOException {
		super(MetadataFormat.DC, text);
	}

	public DCRecord(String id) {
		super(MetadataFormat.DC, id);
	}

}
