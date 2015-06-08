package com.cervantesvirtual.metadata;

/**
 * A metadata field.
 * 
 * @author RCC.
 * @verion 2011.03.10
 */
public interface Field {

    /**
     * Get the field format
     * @return the metadata format
     */
    public MetadataFormat getFormat();

    /**
     * Get the field type.
     * @return the field type.
     */
    public FieldType getType();

    /**
     * Get the field tag.
     * @return the field tag.
     */
    public String getTag();

    /**
     * Get the field content.
     * @return the field content.
     */
    public String getValue();

    /**
     * @return XML representation
     */
    public String toXML();

    /**
     * @return string representation.
     */
    @Override
    public String toString();

    /**
     * @param o another object
     * @return true if all subfields are identical.
     */
    @Override
    public boolean equals(Object o);

    /**
     * @return a hash code consistent with equality.
     */
    @Override
    public int hashCode();
}