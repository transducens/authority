package com.cervantesvirtual.metadata;

import org.w3c.dom.Node;
import com.cervantesvirtual.util.Normalizer;

/**
 * A MARC controlfield.
 *
 * @author RCC.
 * @version 2011.03.30
 */
public class MARCControlField implements Field {

    String tag;
    char ind1;
    char ind2;
    String value;

    /**
     * Full constructor
     */
    public MARCControlField(String tag, char ind1, char ind2, String value) {
        this.tag = tag;
        this.ind1 = ind1;
        this.ind2 = ind2;
        this.value = value;
    }

    /**
     * Get an attribute from XML element or null if the attribute is not defined
     */
    private String getAttributeValue(Node node, String name) {
        String avalue = null;
        org.w3c.dom.NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node attnode = attributes.getNamedItem(name);
            if (attnode != null) {
                avalue = attnode.getNodeValue();
            }
        }
        return avalue;
    }

    /**
     * Constructor from XML nodes
     */
    public MARCControlField(Node field) {
        String attvalue;
        tag = getAttributeValue(field, "tag");
        attvalue = getAttributeValue(field, "ind1");
        ind1 = (attvalue == null) ? ' ' : attvalue.charAt(0);
        attvalue = getAttributeValue(field, "ind2");
        ind2 = (attvalue == null) ? ' ' : attvalue.charAt(0);
        value = field.getTextContent();
    }

    /**
     * Constructor from string
     *
     * @param heading the field tag and optional indicators.
     * @param content the field value.
     */
    public MARCControlField(String heading, String value) {
        tag = heading.substring(0, 3);
        this.value = value;

        if (heading.length() > 3) {
            ind1 = heading.charAt(3);
        } else {
            ind1 = ' ';
        }
        if (heading.length() > 4) {
            ind2 = heading.charAt(4);
        } else {
            ind2 = ' ';
        }
    }

    /**
     * Get the field type.
     */
    @Override
    public FieldType getType() {
        return FieldType.MARC_CONTROLFIELD;
    }

    /**
     * Get the field format
     */
    @Override
    public MetadataFormat getFormat() {
        return MetadataFormat.MARC;
    }

    /**
     * Get the field tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Get the field value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get indicator 1
     */
    public char getInd1() {
        return ind1;
    }

    /**
     * Get indicator 2
     */
    public char getInd2() {
        return ind2;
    }

    /**
     * @return XML representation
     */
    public String toXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<marc:controlfield tag=\"" + tag + "\" ");
        builder.append("ind1=\"" + ind1 + "\" ");
        builder.append("ind2=\"" + ind2 + "\">");
        builder.append(Normalizer.encode(value));
        builder.append("</marc:controlfield>");
        return builder.toString();
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        return tag + ind1 + ind2 + " " + Normalizer.encode(value);
    }

    /**
     * @return true if ControlFields are identical to each other.
     */
    public boolean equals(MARCControlField other) {
        return this.tag.equals(other.tag)
                && this.ind1 == other.ind1
                && this.ind2 == other.ind2
                && this.value.equals(other.value);
    }

    /**
     * @return true if ControlFields are identical to each other.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else {
            return this.equals((MARCControlField) o);
        }
    }

    /**
     * @return a hash code consistent with equality
     */
    public int hashCode() {
        return tag.hashCode() ^ 31 * value.hashCode();
    }
}