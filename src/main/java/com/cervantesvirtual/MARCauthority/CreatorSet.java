package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.io.Messages;
import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.Field;
import com.cervantesvirtual.metadata.FieldType;
import com.cervantesvirtual.metadata.MARCDataField;
import com.cervantesvirtual.metadata.Record;
import com.cervantesvirtual.util.MultiTreeMap;
import com.cervantesvirtual.util.StringFinder;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A set storing all creators and variants (extended MARCDataFields).
 */
public class CreatorSet extends HashSet<Creator> {

    static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public CreatorSet() {
        super();
    }

    /**
     * Create the set from a MARC bibliographic record.
     *
     * @param record A MARC bibliographic record.
     */
    public CreatorSet(Record record) {
        super();
        add(record);
    }

    /**
     * Create the set from a collection of MARC bibliographic records.
     *
     * @param collection The collection of creations.
     */
    public CreatorSet(Collection collection) {
        super();
        add(collection);
    }

    /**
     * Add all creators in MARC bibliographic record.
     *
     * @param record A MARC bibliographic record.
     */
    public final void add(Record record) {
        for (Field field : record.getFields()) {
            if (field.getType() == FieldType.MARC_DATAFIELD) {
                MARCDataField datafield = (MARCDataField) field;
                String tag = datafield.getTag();
                if (tag.matches("[17][01][01]")) {
                    Creator creator = new Creator(datafield);
                    add(creator);
                }
            }
        }
    }

    /**
     * Add all creators in a collection of MARC bibliographic records.
     *
     * @param collection The collection of creations to be added.
     */
    public final void add(Collection collection) {
        for (Record record : collection.getRecords()) {
            add(record);
        }
    }

    /**
     * Find alternative names with under 1 typo per word
     *
     * @param creator A creator.
     * @return list of similar creators
     */
    public List<Creator> findSimilar(Creator creator) {
        MultiTreeMap<Double, Creator> map = new MultiTreeMap<>();

        for (Creator altcreator : this) {
            double sim = creator.similarity(altcreator);
            if (sim <= 1.2) {
                map.add(sim, altcreator);
            }
        }
        return map.values();
    }

    /**
     * Find names in this set with under 25% typos Uses StringFinder for faster
     * retrieval of similar strings.
     *
     * @param writer the writer to dump the information
     */
    public void printSimilar(PrintWriter writer) {
        StringFinder finder = new StringFinder();
        // Partition of creators according to their names.
        Map<String, Set<Creator>> creators = new HashMap<>();

        for (Creator creator : this) {
            String name = creator.getFullName();
            Set<String> alternatives = finder.select(name, name.length() / 4);
            
            //Messages.info(name);
            for (String alternative : alternatives) {
                // System.out.println(name + " * " + alternative);
                for (Creator altcreator : creators.get(alternative)) {
                    double sim = creator.similarity(altcreator);
                    if (sim > 0 && sim <= 1.0) {
                        writer.printf("%.2f", sim).println(" | "
                                + creator + " | " + altcreator);
                    }
                    if (name.equals(altcreator.getFullName())
                            && !creator.compatible(altcreator)) {
                        writer.printf("* %.2f", sim).println(" | "
                                + creator + " | " + altcreator);
                    }

                }
            }

            if (creators.containsKey(name)) {
                creators.get(name).add(creator);
            } else {
                Set<Creator> set = new HashSet<>();
                set.add(creator);
                creators.put(name, set);
            }
            finder.add(name);
        }
        writer.close();
    }
    /*
     public static void main(String[] args) throws java.io.IOException {
     if (args.length < 1) {
     System.err.println("Usage: CreatorSet files.xml [name]");
     } else {
     CreatorSet creators = new CreatorSet();
     for (String arg : args) {
     File dir = new File(arg);
     if (dir.isDirectory()) {
     for (File file : dir.listFiles()) {
     if (file.getName().endsWith(".xml")) {
     Collection bibliographic = new Collection(
     MetadataFormat.MARC,
     DocumentParser.parse(file));
     creators.add(bibliographic);
     } else {
     Creator creator = new Creator(arg);
     System.err.println(creators.findSimilar(creator));
     }
     }
     } else if (dir.getName().endsWith(".xml")) {
     Collection bibliographic = new Collection(
     MetadataFormat.MARC, DocumentParser.parse(dir));
     creators.add(bibliographic);
     } else {
     Creator creator = new Creator(arg);
     System.err.println(creator + " : " + creators.findSimilar(creator));
     }
     }
     //creators.findSimilar();
     }
     }
     */
}
