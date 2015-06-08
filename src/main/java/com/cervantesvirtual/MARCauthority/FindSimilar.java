package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.MetadataFormat;
import java.io.File;
import java.io.PrintWriter;

/**
 * Control of authorities: find occurrences of similar creators in authority
 * file.
 */
public class FindSimilar {

    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 2) {
            System.err.println("Usage FindSimilar input.xml output.csv");
        } else {
            // The document with the authority records.
            File infile = new File(args[0]);
            PrintWriter writer = new PrintWriter(args[1]);
            
            // The authority collection
            Collection collection = new Collection(MetadataFormat.MARC, infile);
            
            CreatorSet set = new CreatorSet(collection);
            System.err.println("Analysing " + set.size() + " creators");
            set.printSimilar(writer);
        }
    }
}
