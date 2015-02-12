package com.cervantesvirtual.metadata;

import com.cervantesvirtual.xml.DocumentParser;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create a DC record from a MARC record.
 *
 * @author RCC
 * @version 2011.03.14
 */
public class Transformer {

    static Map<DCTerm, String> selection;
    static Pattern ypattern; // pattern for years contained in datafields

    static {
        ypattern = Pattern
                .compile("(.*)(\\p{Space}|^)(1[0-9]{3}|20[0-1][0-9])(\\p{Space}|[,;.]|$)(.*)");
    }

    /**
     * Transform a MARC collection into a DC collection.
     *
     * @param source the source MARC collection.
     * @return The equivalent DC collection.
     */
    public static Collection transform(Collection source) {
        Collection target = new Collection(MetadataFormat.DC);
        if (source.getFormat() == MetadataFormat.MARC) {
            for (Record record : source.getRecords()) {
                target.add(transform(record));
            }
        } else {
            System.err.println("Cannot transform non-MARC collection");
        }
        return target;
    }

    /**
     * Parenthesize dates in names
     *
     * @return name with corrected date parenthesization
     */
    public static String getName(MARCDataField mfield) {
        String content = getTextContent(mfield, "-$0");
        String dates = getTextContent(mfield, "$d").trim()
                .replaceAll("[()]", "");
        if (dates.length() > 0) {
            return content.replace(dates, "(" + dates + ")");
        } else {
            return content;
        }
    }

    /**
     * Transform a MARC record into a DC record.
     *
     * @param record
     * @return
     */
    public static Record transform(Record record) {
        String id = record.getId();
        Record target = new Record(MetadataFormat.DC, id);
        target.addField(new DCField(DCTerm.IDENTIFIER, id));
        for (Field field : record.getFields()) {
            String tag = field.getTag();
            if (field.getType() == FieldType.MARC_DATAFIELD) {
                MARCDataField mfield = (MARCDataField) field;
                if (tag.equals("100") || tag.equals("110") || tag.equals("111")) {
                    String content = getTextContent(mfield, "-$0");
                    String dates = getTextContent(mfield, "$d").trim()
                            .replaceAll("[()]", ""); // TODO make test
                    String name = getTextContent(mfield, "$a $b $c");

                    if (dates.length() > 0) {
                        target.addField(new DCField(DCTerm.CREATOR_DATE, dates));
                        target.addField(new DCField(DCTerm.CREATOR, content
                                .replace(dates, "(" + dates + ")")));
                    } else {
                        target.addField(new DCField(DCTerm.CREATOR, content));
                    }
                    target.addField(new DCField(DCTerm.CREATOR_NAME, name));
                } else if (tag.equals("700") || tag.equals("710")
                        || tag.equals("711")) {
                    DCTerm dctag = hasSubfield(mfield, "$e:coaut") ? DCTerm.CREATOR
                            : DCTerm.CONTRIBUTOR;
                    String content = getTextContent(mfield, "-$0");
                    String dates = getTextContent(mfield, "$d");
                    String name = getTextContent(mfield, "$a $b $c");
                    if (dates.length() > 0) {
                        target.addField(new DCField(DCTerm.CONTRIBUTOR_DATE,
                                dates));
                        target.addField(new DCField(dctag, content.replace(
                                dates, "(" + dates + ")")));
                    } else {
                        target.addField(new DCField(dctag, content));
                    }
                    target.addField(new DCField(DCTerm.CONTRIBUTOR_NAME, name));
                } else if (tag.equals("511")) {
                    target.addField(new DCField(DCTerm.CONTRIBUTOR,
                            getTextContent(mfield, "$a")));
                } else if (tag.equals("130") || tag.equals("240")
                        || tag.equals("245")) {
                    target.addField(new DCField(DCTerm.TITLE, getTextContent(
                            mfield, "$a $b $n $p")));
                    if (tag.equals("245")) { // grabación sonora, manuscrito...
                        target.addField(new DCField(DCTerm.TYPE,
                                getTextContent(mfield, "$h")));
                    }
                } else if (tag.equals("246")) {
                    target.addField(new DCField(DCTerm.ALTERNATIVE,
                            getTextContent(mfield, "$a $b $n $p")));
                } else if (tag.equals("080")) {
                    target.addField(new DCField(DCTerm.SUBJECT_UDC,
                            getTextContent(mfield, "$a")));
                } else if (tag.equals("600") || tag.equals("650")
                        || tag.equals("651")) {
                    target.addField(new DCField(DCTerm.SUBJECT_LCSH,
                            getTextContent(mfield, "$a $x $y $t", " - ")));
                    target.addField(new DCField(DCTerm.SUBJECT, getTextContent(
                            mfield, "$a $x $y $t", " - ")));
                } else if (tag.equals("596")) {
                    target.addField(new DCField(DCTerm.DESCRIPTION,
                            getTextContent(mfield, "$a")));
                } else if (tag.equals("260")) {
                    target.addField(new DCField(DCTerm.PUBLISHER,
                            getTextContent(mfield, "$a $b $c")));
                    String publicationDate = getTextContent(mfield, "$c");
                    if (publicationDate.length() > 0) {
                        target.addField(new DCField(DCTerm.ISSUED,
                                publicationDate));
                    }
                } else if (tag.equals("534")) { // source edition statement
                    List<MARCSubfield> subfields = mfield.getSubfields();
                    StringBuilder source = new StringBuilder();
                    StringBuilder altsource = new StringBuilder(); // alternative
                    // source
                    boolean alt = false;
                    for (MARCSubfield subfield : subfields) {
                        if (subfield.getCode() == 'c') {
                            Matcher matcher = ypattern.matcher(subfield
                                    .getValue());
                            if (matcher.matches()) {
                                String year = matcher.group(3);
                                target.addField(new DCField(DCTerm.CREATED,
                                        year));
                                target.addField(new DCField(DCTerm.DATE, year));
                            }
                        } else if (subfield.getCode() == 'p') {
                            if (subfield.getValue().contains("otra ed")) {
                                alt = true;
                            } else {
                                alt = false;
                            }
                        }
                        if (alt) {
                            if (altsource.length() > 0) {
                                altsource.append(' ');
                            }
                            altsource.append(subfield.getValue());
                        } else {
                            if (source.length() > 0) {
                                source.append(' ');
                            }
                            source.append(subfield.getValue());
                        }
                    }
                    if (source.length() > 0) {
                        target.addField(new DCField(DCTerm.SOURCE, source
                                .toString()));
                    }
                    if (altsource.length() > 0) {
                        target.addField(new DCField(DCTerm.RELATION, altsource
                                .toString()));
                    }
                } else if (tag.equals("901")
                        && hasSubfield((MARCDataField) field,
                                "$a=sourceTypeOriginal_L")) {
                    target.addField(new DCField(DCTerm.FORMAT, getTextContent(
                            mfield, "$b")));
                } else if (tag.equals("020")) {
                    target.addField(new DCField(DCTerm.SOURCE, "ISBN:"
                            + getTextContent(mfield)));
                } else if (tag.equals("022")) {
                    target.addField(new DCField(DCTerm.SOURCE, "ISSN:"
                            + getTextContent(mfield)));
                } else if (tag.equals("041")) {
                    target.addField(new DCField(DCTerm.LANGUAGE,
                            getTextContent(mfield)));
                } else if (tag.equals("017")) {
                    target.addField(new DCField(DCTerm.RIGHTS,
                            getTextContent(mfield)));
                } else if (tag.equals("440") || tag.equals("490")) {
                    target.addField(new DCField(DCTerm.IS_PART_OF,
                            getTextContent(mfield, "$a")));
                } else if (tag.equals("655")) {
                    target.addField(new DCField(DCTerm.TYPE,
                            getTextContent(mfield)));
                } else if (tag.equals("773")) {
                    target.addField(new DCField(DCTerm.RELATION,
                            getTextContent(mfield, "$t")));
                }
            }
        }
        if (target.getFields(DCTerm.DATE.toString()).isEmpty()) {
            for (Field field : target.getFields(DCTerm.ISSUED.toString())) {
                target.addField(new DCField(DCTerm.DATE, field.getValue()));
            }
        }
        return target;
    }

    /**
     * Get the textual content of a MARCDataField.
     *
     * @param field a MARCDataField
     * @return The subfield values in the order they are found in the field.
     */
    public static String getTextContent(MARCDataField field) {
        StringBuilder builder = new StringBuilder();
        for (MARCSubfield subfield : field.getSubfields()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(subfield.getValue());
        }
        return builder.toString();
    }

    /**
     * Get the textual content of a selection of subfields in a MARCDataField.
     *
     * @param field a MARCDataField
     * @param selection a string of codes such as "$a $d", optionally preceded
     * by a dash, "-$a $d", which denotes exclusion.
     * @return The subfield values whose code is contained in the selection, in
     * the order they are found in the field. If the selection starts with a
     * dash then, all subfields are selected except for those in the selection.
     */
    protected static String getTextContent(MARCDataField field, String selection) {
        return getTextContent(field, selection, " ");
    }

    /**
     * Get the textual content of a selection of subfields in a MARCDataField.
     *
     * @param field a MARCDataField
     * @param selection a string of codes such as "$a $d", optionally preceded
     * by a dash, "-$a $d", which denotes exclusion.
     * @param separator the separator string between subfields
     * @return The subfield values whose code is contained in the selection, in
     * the order they are found in the field. If the selection starts with a
     * dash then, all subfields are selected except for those in the selection.
     */
    protected static String getTextContent(MARCDataField field, String selection,
            String separator) {
        StringBuilder builder = new StringBuilder();
        boolean include = selection.startsWith("-") ? false : true;
        for (MARCSubfield subfield : field.getSubfields()) {
            boolean selected = selection.contains("$" + subfield.getCode());
            if (include && selected || (!include && !selected)) {
                if (builder.length() > 0) {
                    builder.append(separator);
                }
                builder.append(subfield.getValue());
            }
        }
        return builder.toString();
    }

    /**
     * Get the textual content of a selection of subfields in a MARCDataField.
     *
     * @param field a MARCDataField
     * @param selection array of prefix of codes such as {"$a","$d"}.
     * @return The values of the first subfields in the field whose prefix codes
     * are contained in the selection, in the order they are selected.
     */
    @SuppressWarnings("unused")
    protected static String getTextContent(MARCDataField field, String[] selection) {
        StringBuilder builder = new StringBuilder();
        for (String select : selection) {
            for (MARCSubfield subfield : field.getSubfields()) {
                if (select.equals("$" + subfield.getCode())) {
                    if (builder.length() > 0) {
                        builder.append(' ');
                    }
                    builder.append(subfield.getValue());
                    break; // first has been found!
                }
            }
        }
        return builder.toString();
    }

    /**
     * @param subfield a MARC subfield (e.g., "$a Mariano José de Larra").
     * @param pattern the content-pattern (e.g., "$a:de Larra", "$a=Larra",
     * "$a+Larra" or "$a-Larra")
     * @return true if the string contains ($a: $a+ $a-) or matches ($a=) the
     * pattern.
     */
    private static boolean matches(MARCSubfield subfield, String pattern) {
        String[] token = pattern.split("[+-:=]", 2);

        if (token.length != 2) {
            System.err.println(pattern + " is not a valid content filter");
            System.err.println(java.util.Arrays.toString(token));
            return false;
        } else {
            String head = token[0];
            String tail = token[1];
            if (head.equals("$" + subfield.getCode())) {
                if (pattern.startsWith(head + "=")) {
                    return subfield.getValue().equals(tail);
                } else { // +,-,:
                    return subfield.getValue().contains(tail);
                }
            } else {
                return false;
            }
        }
    }

    /**
     * Performs OR over all subfields in a MARCDataField
     *
     * @param field a MARC field.
     *
     * @param pattern the content-pattern.
     * @return true if at least one subfield matches the content-pattern
     */
    protected static boolean hasSubfield(MARCDataField field, String pattern) {
        for (MARCSubfield subfield : field.getSubfields()) {
            if (matches(subfield, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Performs OR/AND over patterns (OR for :=, AND for +-)
     *
     * @param filed the MARCDataField to be compared with the content-patterns
     * @param patterns the list of content-patterns
     */
    private static boolean matchesAllPatterns(MARCDataField field,
            String[] patterns) {
        if (patterns.length > 0) {
            boolean bor = false; // true if matching [:=] is found
            boolean band = false; // true if matching [+-] is found
            for (String pattern : patterns) { // AND-OR-loop
                if (pattern.length() > 0) {
                    String head = pattern.split("[+-:=]", 2)[0];
                    if (head.length() < pattern.length()) {
                        char type = pattern.charAt(head.length());
                        boolean b = hasSubfield(field, pattern);
                        if (type == '+' || type == '-') {
                            if ((type == '-' && b) || (type == '+' && !b)) {
                                return false;
                            } else {
                                band = true;
                            }
                        } else {
                            bor = bor || b;
                        }
                    }
                }
            }
            return band || bor;
        } else {
            return true;
        }
    }

    /**
     * @param record a MARC record.
     * @param tags the selected field tags.
     * @param patterns array of content requirements of the form "$a:de Larra"
     * (or contains), "$a=Larra" (or exact), "$a+Larra" (and contains)
     * "$a-Larra" (and excludes).
     * @return The list of fields with a tag in the array of tags matching all
     * the patterns.
     */
    public static List<Field> getFields(Record record, String[] tags,
            String[] patterns) {
        List<Field> res = new java.util.ArrayList<Field>();

        for (String tag : tags) {
            for (Field field : record.getFields(tag)) {
                if (matchesAllPatterns((MARCDataField) field, patterns)) {
                    res.add(field);
                }
            }
        }
        return res;
    }

    /**
     * @param list a repeatable field such as {"$a name", "$d dates", "$$", "$a
     * anothername"}.
     * @param selection array of prefix of codes such as {"$a","$d"}.
     * @param patterns array of content requirements of the form "$a:de Larra"
     * (contains), "$a=Larra" (exact) or "$a-Larra"(exclude).
     * @return for every field matching the all patterns, the subfield values
     * whose prefix code is contained in the selection, in the order they are
     * selected.
     *
     * public static List<String> apply(List<String> list, String[] selection,
     * String[] patterns) { List<String> res = new ArrayList<String>(); String
     * selected;
     *
     * while (list.size() > 0) { int last = list.indexOf(separator); if (last <
     * 0) { if (matchesAllPatterns(list, patterns)) { selected = toString(list,
     * selection); if (selected.length() > 0) { res.add(selected); } } list =
     * list.subList(0, 0); } else { List<String> sublist = list.subList(0,
     * last); if (matchesAllPatterns(sublist, patterns)) { selected =
     * toString(sublist, selection); if (selected.length() > 0) {
     * res.add(selected); } } list = list.subList(last + 1, list.size()); } }
     * return res; }
     */
    /**
     * Transform a MARC collection into a Dublin Core collection
     */
    public static void main(String[] args) throws Exception {
        Collection collection = new Collection(MetadataFormat.MARC);
        Collection dc = new Collection(MetadataFormat.DC);
        for (String arg : args) {
            java.io.File dir = new java.io.File(arg);
            if (arg.endsWith(".xml")) {
                collection = new Collection(MetadataFormat.MARC,
                        DocumentParser.parse(dir));
                for (Record record : collection.getRecords()) {
                    dc.add(com.cervantesvirtual.metadata.Transformer
                            .transform(record));
                }
                System.err.println(dir);
            } else if (dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (file.getName().endsWith(".xml")) {
                        collection = new Collection(MetadataFormat.MARC,
                                DocumentParser.parse(file));
                        for (Record record : collection.getRecords()) {
                            dc.add(com.cervantesvirtual.metadata.Transformer
                                    .transform(record));
                        }
                        System.err.println(file);
                    }
                }
            }
        }
        dc.writeXML(System.out);
    }
}
