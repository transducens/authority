package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.MetadataFormat;
import com.cervantesvirtual.io.Backup;
import com.cervantesvirtual.xml.DocumentParser;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Quick and dirty transformation from bibliographic to authority
 * @author RCC
 */
public class MARCBib2Auth {

    public static void main(String[] args) throws java.io.IOException {
        if (args.length < 1) {
            System.err.print("Usage: AuthorityCollection [-f]\n"
                    + "output_file input_dir_or_files\n"
                    + "Option -f prints only entries with variants");
        } else {
            boolean filter = false;
                        File fout = null;
            FileOutputStream fos = null;
            AuthorityCollection authority = new AuthorityCollection();
            MARCAuthorityBuilder builder = new MARCAuthorityBuilder();

            for (String arg : args) {
                if (arg.equals("-f")) {
                    filter = true;
                } else if (fos == null) {
                    fout = new File(arg);
                    Backup.file(fout);
                    fos = new FileOutputStream(fout);
                } else {
                    File dir = new File(arg);
                    if (dir.isDirectory()) {
                        for (File file : dir.listFiles()) {
                            if (file.getName().endsWith(".xml")) {
                                Collection bibliographic = new Collection(
                                        MetadataFormat.MARC,
                                        DocumentParser.parse(file));
                                builder.addBibliographicCollection(bibliographic);
                            }
                        }
                    } else {
                        Collection bibliographic = new Collection(
                                MetadataFormat.MARC, DocumentParser.parse(dir));
                        builder.addBibliographicCollection(bibliographic);
                    }
                }
            }


            if (filter) { // Print only those with multiple entries
                authority = builder.selectComplexRecords().toAuthorityCollection();
            } else {
                authority = builder.toAuthorityCollection();
            }
            authority.writeXML(fos);

            System.err.println("MARC authority file with "
                    + authority.size() + " entries and " + authority.getFields().size()
                    + " variants dumped to "
                    + fout.getName());
            fos.close();
        }

    }
}
