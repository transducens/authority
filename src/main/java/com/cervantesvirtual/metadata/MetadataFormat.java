package com.cervantesvirtual.metadata;

/**
 * Metadata formats.
 * 
 * @author RCC.
 * @verion 2011.03.10
 */
public enum MetadataFormat {
	MARC, DC, EAD, INDEX;

	/**
	 * @return The prefix used to identify the elements in XML files with the
	 *         namespace for this format.
	 */
	public String getNamespacePrefix() {
		switch (this) {
		case MARC:
			return "marc:";
		case DC:
			return "dcterm:";
		case EAD:
			return "ead:";
		case INDEX:
			return "index:";
		default:
			return "";
		}
	}

	/**
	 * @return The tag used to identify the elements in XML files containing a
	 *         metadata collection.
	 */
	public String getCollectionTag() {
		switch (this) {
		case MARC:
			return "marc:collection";
		case DC:
			return "rdf:RDF";
		case EAD:
			return "ead:collection";
		case INDEX:
			return "index:collection";
		default:
			return "";
		}
	}

	/**
	 * @return The declaration of namespaces employed in the XML files
	 *         containing a metadata collection.
	 */
	public String getNamespaceDeclarations() {
		switch (this) {
		case MARC:
			return "xmlns:marc=\"http://www.loc.gov/MARC21/slim\" "
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim "
					+ "http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\"";
		case DC:
			/*
			 * return "xmlns=\"http://example.org/myapp/\"" +
			 * "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
			 * "xsi:schemaLocation=\"http://example.org/myapp/ " +
			 * "http://example.org/myapp/schema.xsd\"" +
			 * "xmlns:dc=\"http://purl.org/dc/elements/1.1/\"" +
			 * "xmlns:dcterms=\"http://purl.org/dc/terms/\"";
			 */
			return "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
					+ "xmlns:dc=\"http://purl.org/dc/terms/\"";

		case EAD:
		case INDEX:
		default:
			return "";
		}
	}

	/**
	 * @return The tag used to identify the elements in XML files containing a
	 *         metadata record.
	 */
	public String getRecordTag() {
		switch (this) {
		case MARC:
			return "marc:record";
		case DC:
			return "rdf:Description";
		case EAD:
			return "ead:record";
		case INDEX:
			return "index:card";
		default:
			return "";
		}
	}

	/**
	 * @return The format's name in lowercase.
	 */
	public String toLowerCase() {
		return toString().toLowerCase();
	}

}