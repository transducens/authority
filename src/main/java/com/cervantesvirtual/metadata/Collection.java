package com.cervantesvirtual.metadata;

import com.cervantesvirtual.io.CSVReader;
import com.cervantesvirtual.io.Messages;
import com.cervantesvirtual.xml.DocumentParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A metadata collection.
 *
 * @author RCC.
 * @version 2011.03.10
 */
public class Collection {

    protected MetadataFormat format;
    protected List<Record> records;

    /**
     * Default constructor.
     *
     * @param format the metadata format
     */
    public Collection(MetadataFormat format) {
        this.format = format;
        records = new ArrayList<Record>();
    }

    /**
     * Create a collection from a single XML document.
     *
     * @param format the metadata format
     * @param doc the XML document containing the collection
     */
    public Collection(MetadataFormat format, Document doc) {
        this.format = format;
        records = new ArrayList<Record>();
        add(format, doc);
    }

    /**
     * Create a collection from a single CSV document.
     *
     * @param format the metadata format
     * @param csv the CSV document containing the collection (one record per
     * line)
     */
    public Collection(MetadataFormat format, CSVReader csv) {
        this.format = format;
        records = new ArrayList<Record>();
        add(format, csv);
    }

    /**
     * Create a collection from a file
     *
     * @param format the metadata format
     * @param file the input file
     * @throws java.io.FileNotFoundException
     */
    public Collection(MetadataFormat format, File file)
            throws FileNotFoundException {
        String name = file.getName();

        this.format = format;
        this.records = new ArrayList<Record>();
        
        if (file.isDirectory()) {
            for(File subfile: file.listFiles()) {
                add(format, subfile);
            }
            Messages.info("Read all files in " + file.getName());
        } else {
            add(format, file);       
        }
    }
    
    /** 
     * Add a single file to the collection
     * @param format
     * @param file 
     */
    private void add(MetadataFormat format, File file) throws FileNotFoundException {
        String name = file.getName();
        
         if (name.endsWith(".xml")) {
            add(format, DocumentParser.parse(file));
        } else if (name.endsWith(".csv")) {
            add(format, new CSVReader(file, '\t')); // TAB delimiter is used
        } else {
            System.err.println("Unrecognised file extension in " + name);
        }
    }

    /**
     * Add an XML document to the collection.
     *
     * @param format the format (DC, MARC, EAD) of the metadata.
     * @param doc the container document.
     */
    public final void add(MetadataFormat format, Document doc) {
        if (this.format != format) {
            System.err.println("Format incompatible with metadata collection");
        } else {
            try {
                String tag = format.getRecordTag();
                NodeList nodes = doc.getElementsByTagName(tag);
                for (int n = 0; n < nodes.getLength(); ++n) {
                    Node node = nodes.item(n);
                    records.add(new Record(format, node));
                }
            } catch (MetadataException e) {
                System.err.println("Could not read " + doc.getDocumentURI());
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Add a CSV document to the collection.
     *
     * @param format the format (DC, MARC, EAD) of the metadata.
     * @param csv the CSV parser
     *
     */
    public final void add(MetadataFormat format, CSVReader csv) {
        String[] header;
        try {
            header = csv.getValues(); // read header
            while (csv.ready()) {
                Record record = new Record(format, "");
                String[] values = csv.getValues();
                for (int n = 0; n < values.length; ++n) {
                    Field field = new DCField(header[n], values[n]);
                    record.addField(field);
                }
                if (record.getId().length() > 0) {
                    add(record);
                } else {
                    System.err.println("Input file contains record with no identifier");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file");
            e.printStackTrace();
        }
    }

    /**
     * Merge two collections with the same format
     *
     * @param collection the collection to be added
     */
    public void add(Collection collection) {
        if (collection.format.equals(this.format)) {
            records.addAll(collection.records);
        } else {
            System.err.println("Collections with different format will not be merged");
        }
    }

    /**
     * Read lines until an empty line or the end of file are found.
     *
     * @param reader a BufferedReader
     * @return The content read or null if no content before the empty line was
     * found.
     */
    private String readRecord(BufferedReader reader) {
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null && line.length() > 0) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return builder.length() > 0 ? builder.toString() : null;
    }

    /**
     * Create a collection from an input stream which uses the empty line as
     * record separator
     *
     * @param format the metadata format
     * @param is input stream containing the metadata
     * @throws java.io.IOException
     * @throws com.cervantesvirtual.metadata.MetadataException
     */
    public Collection(MetadataFormat format, InputStream is)
            throws IOException, MetadataException {
        this.format = format;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        records = new ArrayList<Record>();
        String entry;
        while ((entry = readRecord(reader)) != null) {
            Record record = new Record(format, new StringReader(entry));
            records.add(record);
        }
    }

    /**
     * @return The size of the collection.
     */
    public int size() {
        return records.size();
    }

    /**
     * Get the collection format.
     *
     * @return the format of al records in teh collection
     */
    public MetadataFormat getFormat() {
        return format;
    }

    /**
     * Get all records in this collection.
     *
     * @return all the records in the collection
     */
    public List<Record> getRecords() {
        return records;
    }

    /**
     * Add a record to the collection.
     *
     * @param record the record to be added to the collection
     */
    public void add(Record record) {
        records.add(record);
    }

    /**
     * Compare two collections.
     *
     * @param other another collection
     * @return true if they have the same size and the same records in identical
     * order.
     */
    public boolean equals(Collection other) {
        if (this.format != other.format || this.size() != other.size()) {
            return false;
        }
        for (int n = 0; n < size(); ++n) {
            if (!this.records.get(n).equals(other.records.get(n))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare two collections.
     *
     * @param o another collection
     * @return true if they have the same size and the same recods in identical
     * order.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else {
            return this.equals((Collection) o);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.format != null ? this.format.hashCode() : 0);
        hash = 79 * hash + (this.records != null ? this.records.hashCode() : 0);
        return hash;
    }

    /**
     * Write the collection to an OutputStream.
     *
     * @param os the stream for the output
     */
    public void write(OutputStream os) {
        PrintWriter writer = new PrintWriter(os);
        for (Record record : records) {
            writer.println(record.toString());
        }
        writer.flush();
    }

    /**
     * Write the collection to an OutputStream with the specified encoding.
     *
     * @param os the stream for the output
     * @param charsetName the encoding for the output
     * @throws UnsupportedEncodingException
     */
    public void write(OutputStream os, String charsetName)
            throws UnsupportedEncodingException {
        OutputStreamWriter osw = new OutputStreamWriter(os,
                charsetName);
        PrintWriter writer = new PrintWriter(osw);
        for (Record record : records) {
            writer.println(record.toString());
        }
        writer.flush();
    }

    /**
     * Write as XML document.
     *
     * @param os the stream for the output
     */
    public void writeXML(OutputStream os) {
        PrintWriter writer = new PrintWriter(os);
        String tag = format.getCollectionTag();
        String att = format.getNamespaceDeclarations();
        writer.print("<" + tag + " " + att + ">");
        writer.println();
        for (Record record : records) {
            writer.println(record.toXML());
        }
        writer.println("</" + tag + ">");
        writer.flush();
    }

    /**
     * Add a collection of MARCXMLFiles to the collection.
     *
     * @param file a file or directory.
     */
    public void addMARCXML(File file) {
        if (format == MetadataFormat.MARC) {
            if (file.getName().endsWith(".xml")) {
                add(MetadataFormat.MARC, DocumentParser.parse(file));
                System.err.println(file);
            } else if (file.isDirectory()) {
                for (File subfile : file.listFiles()) {
                    if (subfile.getName().endsWith(".xml")) {
                        add(MetadataFormat.MARC, DocumentParser.parse(subfile));
                        System.err.println(subfile);
                    }
                }
            }
        } else {
            System.err.println("Cannot add non-MARC records to MARC collection");
        }
    }
}
