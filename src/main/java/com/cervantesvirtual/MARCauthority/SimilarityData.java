/*
 * Copyright (C) 2014 Universidad de Alicante
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

import com.cervantesvirtual.io.CSVReader;
import com.cervantesvirtual.metadata.MARCDataField;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Impact
 */
public class SimilarityData
{

    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            System.err.println("Usage SimilarityData input.csv output.csv");
        } else
        {
            // The document with the pairs of creators.
            File infile = new File(args[0]);
            
            
            try
            {
                PrintWriter writer = new PrintWriter(args[1]);
                CSVReader incsv = new CSVReader(infile, ';');
                
                String[] values;
                while( (values = incsv.getValues()) != null )
                {
                    String aut1 = values[2];
                    String aut2 = values[3];
                    
                    String heading = aut1.substring(0, 5);
                    String value = aut1.substring(5);
                    
                    Creator creat1 = new Creator(new MARCDataField(heading, value));

                    heading = aut2.substring(0, 5);
                    value = aut2.substring(5);
                    
                    Creator creat2 = new Creator(new MARCDataField(heading, value));
                    
                    String ref = creat1.variants.toString();
                    int[] dist = levenshteinDistance(ref, creat2.variants.toString(0));
                    int sumDist = sumArray(dist);
                    int titleLength = 0;

                    for (int n = 1; n < creat2.variants.size(); ++n) 
                    {
                        int auxSumaDist;                                               
                        int[] auxDist;
                        
                        auxDist = levenshteinDistance(ref, creat2.variants.toString(n));
                        
                        auxSumaDist = sumArray(auxDist);
                        
                        if(auxSumaDist < sumDist)
                        {
                            sumDist = auxSumaDist;
                            dist = auxDist; 
                        }
                    }
                    
                    if (creat1.title != null && creat2.title != null) 
                    {
                        dist = sumArray(dist, levenshteinDistance(creat1.title, creat2.title));
                        
                        titleLength = creat1.title.split(" ").length
                                + creat2.title.split(" ").length;
                    }

                    int numChars = creat1.variants.size() + creat2.variants.size() + titleLength;

                    writer.println(values[0] + " ; " + values[1] + " ; " + values[2] + " ; " 
                        + values[3] + " ; " + dist[0] + " ; " + dist[1] + " ; " + numChars);                    
                }
                
                writer.close();
                
            } catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        }
    }
    
    public static int[] levenshteinDistance(String first, String second) throws Exception 
    {          
        
        //indel/subs
        int[][][] distance = new int[first.length() + 1][second.length() + 1][2];
         
        for(int i=0;i<=first.length();i++)
        {
                distance[i][0][0]=i;
        }
        
        for(int j=0;j<=second.length();j++)
        {
                distance[0][j][0]=j;
        }
        
        for(int i=1;i<=first.length();i++)
        {
            for(int j=1;j<=second.length();j++)
            { 
                int in,del,subs;
                
                in = sumArray(distance[i-1][j]) + 1;
                del = sumArray(distance[i][j-1]) + 1;
                subs = sumArray(distance[i-1][j-1]) + ((first.charAt(i-1)==second.charAt(j-1))?0:1);
                
                if (in <= del && in <= subs)
                {
                    //insercion
                    distance[i][j][0] = distance[i-1][j][0] + 1;
                    distance[i][j][1] = distance[i-1][j][1];
                }
                else if (del <= in && del <= subs)
                {
                    //deletion
                    distance[i][j][0] = distance[i][j-1][0] + 1;
                    distance[i][j][1] = distance[i][j-1][1];
                }
                else if (subs <= in && subs <= del)
                {
                    //substitution
                    distance[i][j][0] = distance[i-1][j-1][0];
                    distance[i][j][1] = distance[i-1][j-1][1] + ((first.charAt(i-1)==second.charAt(j-1))?0:1);
                }
                else
                {                    
                    throw new Exception("Error en distancia");
                }
            }
        }
        
        return distance[first.length()][second.length()];
 
    }
    
    
    public static int sumArray(int[] intArray)
    {
        int result = 0;
        
        for(int num : intArray)
        {
            result +=num;
        }
        
        return result;
    }
    
    public static int[] sumArray(int[] intArray1, int[] intArray2)
    {
        int[] result = new int[Math.max(intArray1.length, intArray2.length)];
        
        for(int i = 0; i<result.length; i++)
        {
            if (intArray1.length<i && intArray2.length<i)                
                result[i] = intArray1[i] + intArray2[i];
            else if(intArray1.length < i)
                result[i] = intArray1[i];
            else if(intArray2.length < i)
                result[i] = intArray2[i];
            else
                result[i] = 0;
        }
        
        return result;
    }
    
    public static String toString(int[] array)
    {
        StringBuilder builder = new StringBuilder();
        
        for (int i : array)
            builder.append(i +",");
        
        builder.deleteCharAt(builder.length()-1);
        
        return builder.toString();
    }
}
