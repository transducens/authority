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

import com.cervantesvirtual.dates.Period;
import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.Field;
import com.cervantesvirtual.metadata.FieldType;
import com.cervantesvirtual.metadata.MARCDataField;
import com.cervantesvirtual.metadata.MetadataFormat;
import com.cervantesvirtual.metadata.Record;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Impact
 */
public class DateCompatibilityHistogram
{
    
    public static void main (String args[]) throws FileNotFoundException
    {
        if (args.length != 2) {
            System.err.println("Usage DateCompatibilityHistogram input.xml output.csv");
        } else {
            // The document with the authority records.
            File infile = new File(args[0]);
            PrintWriter writer = new PrintWriter(args[1]);
            
            // The authority collection
            Collection collection = new Collection(MetadataFormat.MARC, infile);                                            
            
            int fechas = 0;            
            
            List<Record> records = collection.getRecords();  
                        
            Map<String, Set<Creator>> creators = new HashMap<>();           
            
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
                            Creator creator = new Creator(datafield);
                            String name = creator.getFullName();                            

                            if (creators.containsKey(name)) 
                            {                                
                                creators.get(name).add(creator);                                                              
                            } 
                            else 
                            {                                
                                Set<Creator> set = new HashSet<>();
                                set.add(creator);
                                creators.put(name, set);
                            }                            
                        }
                    }
                }
            }    
            
            String autorsNames[] = new String[creators.size()];
            autorsNames = creators.keySet().toArray(autorsNames);
            
            for (int i = 0; i < autorsNames.length; i++)
            {
                Period per1 = new Period();
                //aut1 period
                for (Creator aut1 : creators.get(autorsNames[i]))
                {
                    if(!aut1.period.isUndefined())
                    {
                        per1 = aut1.period;
                        fechas++;
                        break;
                    }
                }                
                                
                if(!per1.isUndefined())
                {
                    int compatible = 0;
                    for (int j = 0; j < autorsNames.length; j++)
                    {
                        if(i != j)
                        {
                            Period per2 = new Period();
                            //aut2 period
                            for (Creator aut2 : creators.get(autorsNames[j]))
                            {
                                if(!aut2.period.isUndefined())
                                {
                                    per2 = aut2.period;                                
                                    break;
                                }
                            }  
                            
                            if (!per2.isUndefined() && per1.compatible(per2))
                            {
                                compatible++;
                            }
                        }
                    }
                writer.printf("%d\n", compatible);
                }
                
            }
            
            System.err.println("Fechas " + fechas);   
            writer.close();
        }
    }
    
}
