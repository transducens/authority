package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.distances.EditDistance;
import com.cervantesvirtual.util.Normalizer;
import com.cervantesvirtual.util.CyclicArray;
import com.cervantesvirtual.dates.DateParser;
import com.cervantesvirtual.dates.Period;
import com.cervantesvirtual.metadata.MARCDataField;
import com.cervantesvirtual.metadata.MARCSubfield;

/**
 * Enhances the field storing data about an entity  
 * with preprocessed data for a more effective information retrieval
 */
public class AuthorityField extends MARCDataField {

    String id; // creator identifier.
    String name; //  name 
    CyclicArray<String> variants; // normalized variants of name
    String title; // title or location
    Period period; // the dates with their estimated uncertainty.

    /**
     * Identify rotations of the name
     * @param name
     * @return A cyclic array
     */
    private CyclicArray<String> normalizedVariants(String name) {
//        String[] tokens = normal.split("\\p{Space}+");
        String[] tokens = Normalizer.removeStopwords(name.trim()).split(",");
        for (int n = 0; n < tokens.length; ++n) {
            tokens[n] = Normalizer.normalize(tokens[n]);
        }
        return new CyclicArray<String>(tokens);
    }

    /**
     * Extract key information from the data in this field
     */
    private void parse() {
        StringBuilder builder = new StringBuilder();

        for (MARCSubfield subfield : getSubfields()) {
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

            name = builder.toString();
            variants = normalizedVariants(name);
            if (title == null) { // avoid error when prepended to name
                title = "";
            }
            if (period == null) { // avoid error in comparisons 
                period = new Period();
            }
        }
    }

    /**
     * Constructor from a bibliographic MARCDatafield 
     * containing information about a creator.
     * @param tag the tag of this field (100, 400, 500)
     * @param field the MARCDatafield
     */
    public AuthorityField(String tag, MARCDataField field) {
        super(tag, field.getInd1(), field.getInd2(), field.getSubfields());
        parse();
    }

    /**
     * Build a simple creator instance which has only its name defined.
     * @param tag the tag of the authority field
     * @param name a creators name.
     */
    public AuthorityField(String heading, String name) {
        super(heading, "$a" + name);
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
     * A record with missing title is compatible with the one with title
     * (e.g., Santa Teresa de Jesús & Teresa de Jesús)
     * A rotation of the name (after comma) is also a compatible record 
     * (e.g., Bartolomé de las Casas & de las Casas, Bartolomé)
     */
    public boolean compatible(AuthorityField other) {
        boolean b = this.title == null || other.title == null
                || this.title.equals(other.title);
        return this.variants.equals(other.variants) && b
                && this.period.compatible(other.period);
    }

    /**
     * A measure of the similarity between two authority records
     * based on their names and dates.
     * 
     * @return A similarity which is smaller than one only 
     * if dates are compatible within the default uncertainty 
     * and differences in normalized names are below one character per word.
     */
    public double similarity(AuthorityField other) {
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
     * 
     * @param o Another creator
     * @return True if both datafields have the same content for every subfield.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else {
            AuthorityField other = (AuthorityField) o;
            return this.subfields.equals(other.subfields);
        }
    }

    /**
     * @return a hash code consistent with equality
     */
    @Override
    public int hashCode() {
        return subfields.hashCode();
    }

    /**
     * 
     * @return The subtype of field (established, variant, realted)
     */
    public AuthorityType getAuthorityType() {
        switch(tag.charAt(0)) {
            case 1:
                return AuthorityType.ESTABLISHED;
            case 4:
                return AuthorityType.VARIANT;
            case 5:
                return AuthorityType.RELATED;
            default:
                return null;
        }
    }
    
    /**
     * Make this field the established/authoritative, a variant or a related entry.
     * The tag starts with 1, 4 or 5 respectivley
     */
    public void setAuthorityType(AuthorityType type) {
        switch (type) {
            case ESTABLISHED:
                tag = "1" + tag.substring(1);
                break;
            case VARIANT:
                tag = "4" + tag.substring(1);
                break;
            case RELATED:
                tag = "5" + tag.substring(1);
            default:
                break;
        }

    }
}
