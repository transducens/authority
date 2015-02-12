package com.cervantesvirtual.metadata;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;

/**
 * A metadata record. A MetadataFormat and an id are always required.
 * 
 * @author RCC.
 * @version 2011.03.10
 */
public class Record {

    protected MetadataFormat format;
    protected String id;
    protected List<Field> fields;

    /**
     * Default constructor.
     */
    public Record(MetadataFormat format, String id) {
        this.format = format;
        this.id = id;
        fields = new ArrayList<Field>();
    }

    /**
     * Create a Field from an XML element. Since Field is an interface, the
     * implementation is here instead of in the Field class.
     */
    public static Field buildField(MetadataFormat format, Node node)
            throws MetadataException {

        switch (format) {
            case DC:
                return new DCField(node);
            case MARC:
                String tag = node.getNodeName();
                FieldType type = FieldType.typeFromXMLTag(tag);
                switch (type) {
                    case MARC_LEADER:
                        return new MARCLeader(node);
                    case MARC_CONTROLFIELD:
                        return new MARCControlField(node);
                    case MARC_DATAFIELD:
                        return new MARCDataField(node);
                }
            default:
                return null;
        }
    }

    /**
     * Create a Field from its text fields. Since Field is an interface, the
     * implementation is here instead of in the Field class.
     */
    public static Field buildField(MetadataFormat format, String text) {
        switch (format) {
            case DC:
                String[] items = text.split("\\p{Space}+", 2);
                return new DCField(items[0], items[1]);
            case MARC:
                String heading = text.substring(0, 5);
                String value = text.substring(5);
                FieldType type = FieldType.withMARCTag(heading);

                switch (type) {
                    case MARC_LEADER:
                        return new MARCLeader(text);
                    case MARC_CONTROLFIELD:
                        return new MARCControlField(heading, value);
                    case MARC_DATAFIELD:
                        return new MARCDataField(heading, value);
                }
            default:
                return null;
        }
    }

    /**
     * Construct a record from an XML node.
     * 
     * @param record the XMLnode containing the record's elements.
     */
    public Record(MetadataFormat format, Node node) throws MetadataException {
        NodeList children = node.getChildNodes();
        this.format = format;
        fields = new ArrayList<Field>();
        for (int n = 0; n < children.getLength(); ++n) {
            Node child = children.item(n);
            // avoid text node with comments, whitespace, line breaks, etc.
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Field field = buildField(format, children.item(n));
                addField(field);
            }
        }
        if (id == null) {
            System.err.println("Missing id in record " + node.getTextContent());
        }
    }

    /**
     * Construct from a text reader.
     * 
     * @param text
     *            the record's text.
     */
    public Record(MetadataFormat format, java.io.Reader text)
            throws java.io.IOException {
        BufferedReader reader = new BufferedReader(text);

        this.format = format;
        fields = new ArrayList<Field>();

        if (format == MetadataFormat.MARC && reader.ready()) {
            addField(new MARCLeader(reader.readLine()));
        }
        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null) { // ???
                break;
            } else {
                Field field = buildField(format, line);
                addField(field);
            }
        }
    }

    /**
     * Add a field to this record. Revise id recognition!
     */
    public final void addField(Field field) {
        fields.add(field);
        if (FieldType.isIdentifier(field)) {
            id = field.getValue();
        }
    }

    /**
     * Add a field to this record at the given position. 
     * @param index index at which the specified field is to be inserted
     */
    public void addField(int index, Field field) {
        fields.add(index, field);
        if (FieldType.isIdentifier(field)) {
            id = field.getValue();
        }
    }

    /**
     * Merge two records into a single one, provided that they have identical identifiers.
     * Fields are sorted in no particular order.
     * @param other a second Record
     */
    public void merge(Record other) {
        if (this.id.equals(other.id)) {
            for (Field field : other.getFields()) {
                fields.add(field);
            }
        }
    }

    /**
     * @return the number of fields in this record.
     */
    public int size() {
        return fields.size();
    }

    /**
     * Get the record format
     */
    public MetadataFormat getFormat() {
        return format;
    }

    /**
     * Get the record id.
     */
    public String getId() {
        return id;
    }

    /**
     * Get all fields in this record.
     * @return The list of fields in this record
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Get all fields in this record with a given tag.
     */
    public List<Field> getFields(String tag) {
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if (tag.equalsIgnoreCase(field.getTag())) {
                result.add(field);
            }
        }
        return result;
    }

    /**
     * Get all fields in this record whose tag matches a regular expression
     */
    public List<Field> getFieldsMatching(String tag) {
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.getTag().matches(tag)) {
                result.add(field);
            }
        }
        return result;
    }

    /**
     * Compare two records.
     * 
     * @return true if both records are identical.
     */
    public boolean equals(Record other) {
        if (this.fields.size() != other.fields.size()) {
            return false;
        }
        for (int n = 0; n < fields.size(); ++n) {
            if (!this.fields.get(n).equals(other.fields.get(n))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare two records.
     * 
     * @return true if both records are identical.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else {
            return this.equals((Record) o);
        }
    }

    /**
     * @return a hash code consistent with equality
     */
    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    /**
     * @return XML representation.
     */
    public String toXML() {
        StringBuilder builder = new StringBuilder();
        String tag = format.getRecordTag();
        builder.append("<").append(tag).append(">");
        for (Field field : fields) {
            builder.append(field.toXML());
        }
        builder.append("</").append(tag).append(">");
        return builder.toString();
    }

    /**
     * @return string representation.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Field field : fields) {
            builder.append(field.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}