package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.Field;
import com.cervantesvirtual.metadata.MetadataFormat;
import com.cervantesvirtual.metadata.Record;
import com.cervantesvirtual.util.MultiHashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * MARC authoritative collection (from MARC bibliographic metadata)
 * Provides fast access to fields and records through an index
 * The index is a multimap since a field could be in multiple records 
 * as a related entry (sea also tracing)  
 * @author RCC
 */
public class AuthorityCollection extends Collection {

    MultiHashMap<AuthorityField, AuthorityRecord> index; // Fast access to records

    /**
     * Default constructor.
     */
    public AuthorityCollection() {
        super(MetadataFormat.MARC);
        index = new MultiHashMap<AuthorityField, AuthorityRecord>();
    }

    /**
     * Constructor from input file
     * @param filename The containing file
     */
    public AuthorityCollection(String filename) throws FileNotFoundException {
        super(MetadataFormat.MARC, new File(filename));
        index = new MultiHashMap<AuthorityField, AuthorityRecord>();
        for (Record record : getRecords()) {
            AuthorityRecord arecord = new AuthorityRecord(record);
            for (AuthorityField afield : arecord.getAuthorityFields()) {
                index.add(afield, arecord);
            }
        }
    }

    /**
     * Check if a field is already stored in the collection
     * @param field An authorityField describing a creator
     * @return true if the collection contains an identical record, 
     * regardless tag and indicators.
     */
    public boolean contains(AuthorityField field) {
        return index.keyHasValues(field);
    }

    public java.util.Set<AuthorityField> getFields() {
        return index.keySet();
    }

    /**
     * Add the creator stored as an AuthorityField with the AuthorityFiledType 
     * to the adequate AuthorityRecord.
     * @param afield An AuthorityField
     * @param type The AuthorityType
     * @param arecord The container AuthorityRecord
     */
    protected void addFieldToRecord(AuthorityField afield,
            AuthorityType type, AuthorityRecord arecord) {
        index.add(afield, arecord);
        arecord.addAuthorityField(afield, type);
    }

    /**
     * Add a new record to the collection (and index the fields in the record)
     * @param arecord An AuthorityRecord 
     */
    protected void addRecord(AuthorityRecord arecord) {
        add(arecord);
        for (Field field : arecord.getFields()) {
            AuthorityField afield = (AuthorityField) field;
            index.add(afield, arecord);
        }
    }

    /**
     * @return A list of AuthorityRecords containing all forms of the creator.
     */
    public List<AuthorityRecord> getAuthorityRecords() {
        ArrayList<AuthorityRecord> list = new ArrayList<AuthorityRecord>(size());
        for (Record record : getRecords()) {
            if (record instanceof AuthorityRecord) {
                AuthorityRecord arecord = (AuthorityRecord) record;
                list.add(arecord);
            }
        }
        return list;
    }
}
