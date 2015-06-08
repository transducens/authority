/**  
 * This code can be distributed or modified 
 * under the terms of the GNU General Public License V2.
 */
package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.Field;
import com.cervantesvirtual.metadata.Record;
import com.cervantesvirtual.util.Kbselector;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RCC
 */
public class MARCAuthorityBuilder {

    AuthorityCollection acollection;

    /**
     * Default constructor
     */
    public MARCAuthorityBuilder() {
        acollection = new AuthorityCollection();
    }

    /**
     * Build upon an existing authority file
     * @param filename 
     * @throws java.io.FileNotFoundException 
     */
    public MARCAuthorityBuilder(String filename) throws FileNotFoundException {
        acollection = new AuthorityCollection(filename);
    }

    /**
     * Add, interactively, new entry fields in the bibliographic collection 
     * to the authority collection.
     * @param collection The MARC bibliographic collection
     */
    public void addBibliographicCollection(Collection collection) {
        int n = 0;
        for (Record record : collection.getRecords()) {
            System.out.println((++n) + " de " + collection.size());
            for (Field field : record.getFieldsMatching("[17][01][01]")) {
                String tag = "1" + field.getTag().substring(1);
                AuthorityField afield = new AuthorityField(tag, field.getValue());
                if (!acollection.contains(afield)) {
                    try {
                        addAuthorityField(afield);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        return;
                    }
                }
            }
        }

    }

    /**
     * Add a new creator to the collection (not already in the collection)
     * @param afield an AuthorityField describing a creator
     */
    private void addAuthorityField(AuthorityField afield) throws IOException {
        List<AuthorityRecord> candidates = findSimilarRecords(afield);
        AuthorityRecord arecord;
        if (candidates.size() > 0) {
            List<AuthorityRecord> compatible = filterCompatible(candidates, afield);
            if (compatible.size() == 1) {
                arecord = compatible.get(0);
                acollection.addFieldToRecord(afield, AuthorityType.VARIANT, arecord);
            } else {
                arecord = select(afield, candidates);
                if (arecord != null) {
                    acollection.addFieldToRecord(afield, AuthorityType.VARIANT, arecord);
                }
            }
        } else {   // tag 1[01][01]
            String id = Integer.toString(acollection.size());
            arecord = new AuthorityRecord(afield, id);
            acollection.add(arecord);
        }
    }

    /**
     * Find records with names with under 1 typo per word
     * @param afield A creator as an AuthorityField
     */
    private List<AuthorityRecord> findSimilarRecords(AuthorityField afield) {
        List<AuthorityRecord> candidates = new ArrayList<AuthorityRecord>();

        for (AuthorityRecord arecord : acollection.getAuthorityRecords()) {
            if (arecord.similar(afield)) {
                candidates.add(arecord);
            }
        }
        return candidates;
    }

    /**
     * Select those records compatible with an authority field
     * @param alist
     * @param afield
     * @return 
     */
    private List<AuthorityRecord> filterCompatible (List<AuthorityRecord> alist, AuthorityField afield) {
         List<AuthorityRecord> compatible = new ArrayList<AuthorityRecord>();
         for (AuthorityRecord arecord : alist) {
             if (arecord.compatible(afield)) {
                 compatible.add(arecord);
             }
         }
        return compatible;
    }
    
    /**
     * 
     * @param afield The creator's data stored as an AuthorityField
     * @param candidates The list of candidate records to host the creator
     * @return The host record as selected by user
     */
    private AuthorityRecord select(AuthorityField afield, List<AuthorityRecord> candidates)
            throws IOException {
        AuthorityRecord selection = null;
        Integer opt = 0;

        System.out.println("\n0\t[" + afield + "]");
        for (AuthorityRecord arecord : candidates) {
            System.out.println((++opt) + "\t[" + arecord.authorized()
                    + "] " + afield.similarity(arecord.authorized()));
        }

        opt = Kbselector.readOption();
        if (opt != null) { // skip
            if (opt == 0) {
                selection = new AuthorityRecord(afield.getId());
            } else if (Math.abs(opt) <= candidates.size()) {
                selection = candidates.get(opt - 1);
            } else if (opt > 98) {
                throw new IOException("Invalid input interpreted as stop and save.");
            }
        }
        return selection;
    }

    /**
     * Build a collection with only those AuthorityRecords 
     * containing more than one form of the creator.
     * @return 
     */
    public MARCAuthorityBuilder selectComplexRecords() {
        AuthorityCollection collection = new AuthorityCollection();
        for (Record record : acollection.getRecords()) {
            if (record instanceof AuthorityRecord) {
                AuthorityRecord arecord = (AuthorityRecord) record;
                if (arecord.getFieldsMatching("[45][01][01]").size() > 0) {
                    collection.add(arecord);
                }
            }
        }
        acollection = collection;
        return this;
    }

    /**
     * 
     * @return The constructed AuthorityCollection 
     */
    public AuthorityCollection toAuthorityCollection() {
        return acollection;
    }
}
