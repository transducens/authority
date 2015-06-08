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
package com.cervantesvirtual.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Really dummy CSV file reader
 *
 * @author RCC
 *
 */
public class CSVReader extends BufferedReader {
    char delimiter;

    public CSVReader(File file, char delimiter) throws FileNotFoundException {
        super(new FileReader(file));
        this.delimiter = delimiter;
    }

    /**
     * Remove leading/trailing whitespace and (paired) quotes
     *
     * @param s
     * @return
     */
    private String trim(String s) {
        String r = s.trim();
        if (r.startsWith("\"") && r.endsWith("\"")
                || r.startsWith("'") && r.endsWith("'")) {
            return r.substring(1, r.length() - 1);
        } else {
            return r;
        }
    }

    /**
     * @return the array of values in next line or null when end of file has
     * been reached.
     * @throws IOException
     */
    public String[] getValues() throws IOException {
        String line = super.readLine();
        String[] tokens = line.split(String.valueOf(delimiter));
        String[] values = new String[tokens.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = trim(tokens[i]);
        }
        return values;
    }
}
