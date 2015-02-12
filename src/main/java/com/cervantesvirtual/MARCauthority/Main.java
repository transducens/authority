/*
 * This code can be distributed or modified
 * under the terms of the GNU General Public License V2.
 */
package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.MetadataFormat;
import com.cervantesvirtual.io.Backup;
import com.cervantesvirtual.xml.DocumentParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author RCC
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length < 1) {
            System.err.println("Usage: MARCAuthorityBuilder"
                    + "authority_file bibliographic_dir_or_files");
        } else {
            MARCAuthorityBuilder builder = null;
            //Collection skip = new Collection(MetadataFormat.MARC);
            FileOutputStream fos = null;
            for (String arg : args) {
                if (builder == null) {
                    Backup.file(arg);
                    builder = new MARCAuthorityBuilder(arg);
                    try {
                        fos = new FileOutputStream(arg);
                    } catch (Exception e) {
                        System.err.println("Unable to open file " + arg);
                        e.printStackTrace();
                    }
                } else {
                    File dir = new File(arg);
                    if (dir.isDirectory()) {
                        for (File file : dir.listFiles()) {
                            if (file.getName().endsWith(".xml")) {
                                System.err.print("\rProcessing " + file.getName());
                                Collection bibliographic =
                                        new Collection(MetadataFormat.MARC,
                                        DocumentParser.parse(file));
                                builder.addBibliographicCollection(bibliographic);
                                //Collection skipped = collection.addCollection(bibliographic);
                                //skip.add(skipped);
                            }
                        }
                    } else {
                        System.err.println("Processing " + dir.getName());
                        Collection bibliographic =
                                new Collection(MetadataFormat.MARC,
                                DocumentParser.parse(dir));
                         builder.addBibliographicCollection(bibliographic);
                        //Collection skipped = collection.addCollection(bibliographic);
                        //skip.add(skipped);
                    }
                }
            }
            try {
                AuthorityCollection acollection = builder.toAuthorityCollection();
                acollection.writeXML(fos);
                fos.close();
                //fos = new FileOutputStream("skip.xml");
                //skip.writeXML(fos);
                //fos.close();
            } catch (Exception x) {
                System.err.println("Unable to write output files");
            }
        }
    }
}
