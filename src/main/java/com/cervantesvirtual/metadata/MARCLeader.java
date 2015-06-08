package com.cervantesvirtual.metadata;

import org.w3c.dom.Node;

/**
 * A MARC datafield
 * 
 * @author RCC.
 * @verion 2011.03.10
 */
public class MARCLeader implements Field {
	String value;

	/**
	 * Constructor from XML nodes
	 */
	public MARCLeader(Node field) {
		value = field.getTextContent();
	}

	/**
	 * Constructor from string
	 */
	public MARCLeader(String value) {
		this.value = value;
	}

	/**
	 * Get the field format.
	 */
	public MetadataFormat getFormat() {
		return MetadataFormat.MARC;
	}
	
	/**
	 * Get the field type.
	 */
	public FieldType getType() {
		return FieldType.MARC_LEADER;
	}

	/**
	 * Get the field tag.
	 */
	public String getTag() {
		return "";
	}

	/**
	 * Get the field value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return XML representation
	 */
	public String toXML() {
		StringBuilder builder = new StringBuilder();
		builder.append("<marc:leader>");
		builder.append(value);
		builder.append("</marc:leader>");
		return builder.toString();
	}

	/**
	 * @return string representation
	 */
	public String toString() {
		return value;
	}

	/**
	 * @true if Leaders are identical
	 */
	public boolean equals(MARCLeader other) {
		return this.value.equals(other.value);
	}

	/**
	 * @true if Leaders are identical
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		} else {
			return this.equals((MARCLeader) o);
		}
	}
	/**
	 * @return a hash code consistent with equality
	 */
	public int hashCode () {
		return value.hashCode();
	}
}