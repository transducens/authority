package com.cervantesvirtual.metadata;

import com.cervantesvirtual.util.Normalizer;

/**
 * A MARC subfield in a datafield.
 * 
 * @author RCC.
 * @verion 2011.03.10
 */
public class MARCSubfield {
	char code;
	String value;

	/**
	 * Default constructor
	 */
	public MARCSubfield(char code, String value) {
		this.code = code;
		this.value = value;
	}

	/**
	 * Construct subfield from field content, such as "$a John"
	 */
	public MARCSubfield(String text) {
		if (text.matches("[$][a-z0-9].*")) {
			code = text.charAt(1);
			if (text.length() > 1) {
				value = text.substring(2).trim();
			} else {
				value = "";
			}
		}
	}

	/**
	 * Construct subfield from an XML format
	 */
	public MARCSubfield(org.w3c.dom.Node node) {
		code = node.getAttributes().getNamedItem("code").getNodeValue()
				.charAt(0);
		value = node.getTextContent().trim();
	}

	/**
	 * @return The code of the subfield.
	 */
	public char getCode() {
		return code;
	}

	/**
	 * @return The value of the subfield.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return A string representation.
	 */
	public String toString() {
		return "$" + code + Normalizer.encode(value);
	}

	/**
	 * @return XML representation
	 */
	public String toXML() {
		StringBuilder builder = new StringBuilder();
		builder.append("<marc:subfield code=\"");
		builder.append(code);
		builder.append("\">");
		builder.append(Normalizer.encode(value));
		builder.append("</marc:subfield>");
		return builder.toString();
	}

	/**
	 * @return true if subfields are identical.
	 */
	public boolean equals(MARCSubfield other) {
		return this.code == other.code && this.value.equals(other.value);
	}

	/**
	 * @return true if subfields are identical.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		} else {
			return this.equals((MARCSubfield) o);
		}
	}

	/**
	 * @return a hash code consistent with equality
	 */
	@Override
	public int hashCode() {
		return 31 * code ^ value.hashCode();
	}
}