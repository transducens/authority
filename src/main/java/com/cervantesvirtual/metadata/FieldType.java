package com.cervantesvirtual.metadata;

/**
 * The standard types of metadata fields.
 *
 * @author RCC.
 * @verion 2011.03.10
 */
public enum FieldType {

    MARC_LEADER, MARC_CONTROLFIELD, MARC_DATAFIELD, DC_FIELD;

    public static FieldType typeFromXMLTag(String tag) throws MetadataException {
        switch (tag) {
            case "marc:leader":
                return MARC_LEADER;
            case "marc:controlfield":
                return MARC_CONTROLFIELD;
            case "marc:datafield":
                return MARC_DATAFIELD;
            case "dc:field":
                return DC_FIELD;
            default:
                throw new MetadataException("Unsupported field type " + tag);
        }
    }

    /**
     * Identify the type of filed from its MARC tag
     * @param tag a MARC tag
     * @return 
     */
    public static FieldType withMARCTag(String tag) {
        if (tag.matches("[0-9]{3}[0-9 ]{0,2}")) {
            if (tag.substring(0, 3).compareTo("010") < 0) {
                return MARC_CONTROLFIELD;
            } else {
                return MARC_DATAFIELD;
            }
        } else {
            return MARC_LEADER;
        }
    }

    /**
     * Test if the field is a record identifier
     * @param field a metadata field
     * @return true if the field is a record identifier
     */
    public static boolean isIdentifier(Field field) {
        switch (field.getType()) {
            case DC_FIELD:
                return ((DCField) field).getDCTag() == DCTerm.IDENTIFIER;
            case MARC_CONTROLFIELD:
                return field.getTag().equals("001");
            default:
                return false;
        }
    }
}
