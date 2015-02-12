package com.cervantesvirtual.MARCauthority;

import java.util.List;
import java.util.ArrayList;
import com.cervantesvirtual.metadata.Field;
import com.cervantesvirtual.metadata.FieldType;
import com.cervantesvirtual.metadata.MARCControlField;
import com.cervantesvirtual.metadata.MARCDataField;
import com.cervantesvirtual.metadata.MetadataFormat;
import com.cervantesvirtual.metadata.Record;

public class AuthorityRecord extends Record {

    AuthorityField authorized; // the established form.

    /**
     * Default constructor.
     */
    public AuthorityRecord(String id) {
        super(MetadataFormat.MARC, id);
    }

    /**
     * Constructor from a single field
     */
    public AuthorityRecord(AuthorityField field, String id) {
        super(MetadataFormat.MARC, id);
        authorized = field;
        addField(new MARCControlField("001", id));
        addField(field);
    }

    /**
     * Constructor from a MARC (authority) record
     * @param record 
     */
    public AuthorityRecord(Record record) {
        super(MetadataFormat.MARC, record.getId());
        for (Field field : record.getFields()) {
            addField(field);
            if (authorized == null
                    && field.getType() == FieldType.MARC_DATAFIELD) {
                MARCDataField datafield = (MARCDataField) field;
                String tag = datafield.getTag();
                if (tag.matches("100")) {
                    authorized = new AuthorityField("100", datafield);
                }
            }
        }
    }

    /**
     * @return the auhorized form of the creator
     */
    public AuthorityField authorized() {
        return authorized;
    }

    /**
     * @return A list  of AuthorityFields containing all forms of the creator.
     */
    public List<AuthorityField> getAuthorityFields() {
        int size = getFields().size();
        ArrayList<AuthorityField> list = new ArrayList<AuthorityField>(size);
        for (Field field : getFields()) {
            if (field instanceof AuthorityField) {
                AuthorityField afield = (AuthorityField) field;
                list.add(afield);
            }
        }
        return list;
    }

    /** 
     * Check if the creator coincides with one of the forms in the record.
     * 
     * @param creator the creator AuthorityField.
     * @return true if one form in the record equals the creator.
     */
    public boolean contains(AuthorityField creator) {
        for (AuthorityField afield : getAuthorityFields()) {
            if (creator.equals(afield)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if all forms in this record are compatible with a creator.
     * 
     * @param creator the creator AuthorityField.
     * @return true if all forms in this record are compatible with the creator.
     */
    public boolean compatible(AuthorityField creator) {
        for (AuthorityField afield : getAuthorityFields()) {
            if (!creator.compatible(afield)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if at least one form is simialr to thecreator.
     * 
     * @param creator the creator AuthorityField.
     * @return true if the creator is similar to one of the stored forms 
     * (at most 1 typo per word).
     */
    public boolean similar(AuthorityField creator) {
        for (AuthorityField afield : getAuthorityFields()) {
            if (creator.similarity(afield) <= 1.0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check which form is more reliable.
     * 
     * @param first an AuthorityField representing a creator form.
     * @param second an AuthorityField representing a creator form.
     * @return 0 if both creators are not comparable (that is, they are
     *         incompatible); -1 if first contains more information than the
     *         other or its identifier precedes the second's identifier; 1 in
     *         all other cases.
     */
    private int preferred(AuthorityField first, AuthorityField second) {
        if (second != null) {
            if (first.compatible(second)) {
                if (first.period.isUndefined() != second.period.isUndefined()) {
                    return second.period.isUndefined() ? -1 : 1;
                } else {
                    String id1 = first.id;
                    String id2 = second.id;
                    if (id1 == null) {
                        return 1;
                    } else if (id2 == null) {
                        return -1;
                    } else if (id1.length() != id2.length()) {
                        return (id1.length() < id2.length()) ? -1 : 1;
                    } else {
                        return id1.compareTo(id2);
                    }
                }
            } else {
                return 0; // uncomparable
            }
        } else {
            return -1;
        }
    }

    /**
     * Add a creator with the given type 
     * @param afield a creator as an AuthorityFiled
     * @param type the type of field (established, variant, related)
     */
    public void addAuthorityField(AuthorityField afield, AuthorityType type) {
        switch (type) {
            case ESTABLISHED:
                afield.setAuthorityType(AuthorityType.ESTABLISHED);
                if (authorized != null) {
                    authorized.setAuthorityType(AuthorityType.VARIANT);
                }
                addField(0, afield); //
                authorized = afield;
                break;
            case VARIANT:
                afield.setAuthorityType(AuthorityType.VARIANT);
                addField(afield); // append
                break;
            case RELATED:
                afield.setAuthorityType(AuthorityType.RELATED);
                addField(afield);
        }
    }

    /**
     * Add a creator form to this Record.
     * 
     * @param creator the AuthorityField representing the creator form.
     * @param established true if the form is authoritative.
     * @return true if this creator's form is the preferred one.
    
    public final boolean addAuthorityField(AuthorityField afield, boolean established) {
    boolean preferred = false;
    
    if (established) {
    if (preferred(creator, authorized) < 1) {
    afield.setAuthorityType(AuthorityType.ESTABLISHED);
    if (authorized == null) {
    authorized = afield;
    } else {
    authorized.setAuthorityType(AuthorityType.VARIANT);
    authorized = afield;
    }
    
    preferred = true;
    addField(0, afield); // prepend
    } else {
    afield.setAuthorityType(AuthorityType.VARIANT);
    addField(afield); // append
    }
    } else {
    afield.setAuthorityType(AuthorityType.RELATED);
    addField(afield); // append
    }
    
    return preferred;
    }
     */
    /**
     * Get the authoritative name
     * @return the authoritative name 
     */
    public String getCreatorName() {
        return authorized.getFullName();
    }
}
