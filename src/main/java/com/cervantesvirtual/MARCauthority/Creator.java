package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.distances.EditDistance;
import com.cervantesvirtual.util.Normalizer;
import com.cervantesvirtual.util.CyclicArray;
import com.cervantesvirtual.dates.DateParser;
import com.cervantesvirtual.dates.Period;
import com.cervantesvirtual.metadata.MARCDataField;
import com.cervantesvirtual.metadata.MARCSubfield;
import java.util.Arrays;

/**
 * Stores data about a creator.
 */
public class Creator extends MARCDataField {

    String id; // creator identifier.
    String name; //  name 
    CyclicArray<String> variants; // normalized variants of name
    String title; // title or location
    Period period; // the dates with their estimated uncertainty.

    /**
     * Identify rotations of the name
     *
     * @param name
     * @return A cyclic array
     */
    private CyclicArray<String> normalizedVariants(String name) {
//        String[] tokens = normal.split("\\p{Space}+");
        String[] tokens = Normalizer.removeStopwords(name.trim()).split(",");
        System.out.println(name+"->"+Arrays.toString(tokens));
        for (int n = 0; n < tokens.length; ++n) {
            tokens[n] = Normalizer.normalize(tokens[n]);
        }
        return new CyclicArray<String>(tokens);
    }

    /**
     * Constructor from a bibliographic MARCDatafield containing information
     * about a creator.
     *
     * @param field the MARCDatafield
     */
    public Creator(MARCDataField field) {
        super(field);
        StringBuilder builder = new StringBuilder();

        for (MARCSubfield subfield : field.getSubfields()) {
            char code = subfield.getCode();
            if (code == '0') {
                id = subfield.getValue();
            } else if (code == 'a' || code == 'b') {
                if (builder.length() > 0) {
                    builder.append(' ');
                }
                builder.append(subfield.getValue());
            } else if (code == 'c') {
                title = Normalizer.normalize(subfield.getValue());
            } else if (code == 'd') {
                period = DateParser.parse(subfield.getValue());
                if (period == null) {  // if date was unparsable
                    period = new Period();
                }
            }
        }

        name = builder.toString();
        variants = normalizedVariants(name);
        if (period == null) { // required for comparisons (unknown period)
            period = new Period();
        }

    }

    /**
     * Build a simple creator instance which has only its name defined.
     *
     * @param name a creators name.
     */
    public Creator(String name) {
        super("100", "$a" + name);
        this.name = name;
        this.variants = normalizedVariants(name);
        this.title = "";
        this.period = new Period();
    }

    /**
     * @return The creator's identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The creator's name and title
     */
    public String getFullName() {
        return title + " " + name;
    }

    /**
     * @return A hash code consistent with normalized name variants.
     */
    public int nameHashCode() {
        return variants.hashCode();
    }

    /**
     * Check if names and periods are compatible.
     *
     * @param other another creator.
     * @return true if names, titles and periods are compatible.
     */
    public boolean compatible(Creator other) {
        boolean b = this.title == null || other.title == null
                || this.title.equals(other.title);
       
        return this.variants.equals(other.variants) && b
                && this.period.compatible(other.period);
    }

    /**
     * A measure of the similarity between two creators based on their name and
     * dates.
     *
     * @return A similarity which is smaller than one if dates are compatible
     * within the default uncertainty and differences in normalized names are
     * below one character per word.
     */
    public double similarity(Creator other) {
        if (this.period == null) {
            System.err.println("Null period for " + this);
        }
        if (other.period == null) {
            System.err.println("Null period for " + other);
        }
        if (this.period.compatible(other.period)) {
            String ref = this.variants.toString();
            int dist = EditDistance.indelDistance(ref,
                    other.variants.toString(0));
            int titleLength = 0;

            for (int n = 1; n < other.variants.size(); ++n) {
                dist = Math.min(
                        dist,
                        EditDistance.indelDistance(ref,
                                other.variants.toString(n)));
            }
            if (this.title != null && other.title != null) {
                dist += EditDistance.indelDistance(this.title, other.title);
                titleLength = this.title.split(" ").length
                        + other.title.split(" ").length;
            }

            return (2.0 * dist)
                    / (double) (this.variants.size() + other.variants.size() + titleLength);
        } else {
            return 10.1;
        }
    }

    /**
     * Change the field tag.
     *
     * @param tag the new tag.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     *
     * @param o Another creator
     * @return True if both datafields have the same tag (regardless indicators)
     * and content is identical for every subfield.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else {
            Creator other = (Creator) o;
            return this.tag.substring(0, 3).equals(other.tag.substring(0, 3))
                    && this.subfields.equals(other.subfields);
        }
    }

    /**
     * @return a hash code consistent with equality
     */
    @Override
    public int hashCode() {
        return 31 * tag.substring(1, 3).hashCode() ^ subfields.hashCode();
    }
}
