/*
 * Copyright (C) 2015 Impact
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.cervantesvirtual.MARCauthority;

import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.Field;
import com.cervantesvirtual.metadata.FieldType;
import com.cervantesvirtual.metadata.MARCDataField;
import com.cervantesvirtual.metadata.MetadataFormat;
import com.cervantesvirtual.metadata.Record;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Impact
 */
public class AuthorityStatistics
{

    public static void main(String args[]) throws IOException
    {
        if (args.length != 1)
        {
            System.err.println("Usage AuthorityStatistics file");
        } else
        {
            // The document or folder with the authority records.
            File infile = new File(args[0]);

            // The authority collection
            Collection collection = new Collection(MetadataFormat.MARC, infile);

            List<Record> records = collection.getRecords();            

            int allCreators = 0;
            
            for (Record record : records)
            {
                for (Field field : record.getFields())
                {
                    if (field.getType() == FieldType.MARC_DATAFIELD)
                    {
                        MARCDataField datafield = (MARCDataField) field;
                        String tag = datafield.getTag();
                        if (tag.matches("[17][01][01]"))
                        {
                            allCreators++;
                        }
                    }
                }
            }

            //Number of records
            System.out.println("Number of records " + collection.size());
            //Number of creators
            System.out.println("Number of creators " + allCreators);

            /*
             CreatorSet set = new CreatorSet(collection);
             System.err.println("Analysing " + set.size() + " creators");
             */
        }
    }
}
