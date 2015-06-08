package com.cervantesvirtual.metadata;

import org.w3c.dom.Node;
import com.cervantesvirtual.util.Normalizer;

/**
 * A Dublin Core field.
 * 
 * @author RCC
 * @version 2011.03.10
 */
public class DCField implements Field {
	
	DCTerm tag;
	String value;

	/**
	 * Constructor.
	 * 
	 * @param tag
	 *            the field tag.
	 * @param value
	 *            the field value.
	 */
	public DCField(DCTerm tag, String value) {
		this.tag = tag;
		this.value = value;
	}

	/**
	 * Constructor.
	 * 
	 * @param tag
	 *            the field tag.
	 * @param value
	 *            the field value.
	 */
	public DCField(String tag, String value) {
		try {
			this.tag = DCTerm.term(tag);
			this.value = value;
		} catch (MetadataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor from XML elements
	 */
	public DCField(Node node) {
		try {
			tag = DCTerm.term(node.getNodeName());
		} catch (MetadataException e) {
			System.err.println(node.getNodeName() + "is not a DC element");
			e.printStackTrace();
		}
		value = node.getTextContent();
	}

	/**
	 * Get the field format.
	 */
	public MetadataFormat getFormat() {
		return MetadataFormat.DC;
	}

	/**
	 * Get the field type.
	 */
	public FieldType getType() {
		return FieldType.DC_FIELD;
	}

	/**
	 * Get the field tag.
	 */
	public String getTag() {
		return tag.toString();
	}

	/**
	 * Get the field Dublin Core tag.
	 */
	public DCTerm getDCTag() {
		return tag;
	}

	/**
	 * Get the field content.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return true if subfields are identical.
	 */
	public boolean equals(DCField other) {
		return this.tag == other.tag && this.value.equals(other.value);
	}

	/**
	 * @return true if DC fields are identical.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		} else {
			return this.equals((DCField) o);
		}
	}

	/**
	 * @return a hash code consistent with equality
	 */
	public int hashCode () {
		return tag.hashCode() ^ 31 * value.hashCode();
	}
	
	/**
	 * @return XML representation.
	 */
	public String toXML() {
		StringBuilder builder = new StringBuilder();
		String xmlTag = tag.toXMLTag();
		builder.append("<" + xmlTag + ">");
		builder.append(Normalizer.encode(value));
		builder.append("</" + xmlTag + ">");
		return builder.toString();
	}

	/**
	 * @return string representation.
	 */
	public String toString() {
		return tag.toString() + " " + value;
	}

}