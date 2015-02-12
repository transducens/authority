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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Creates a backup copy of the previous version of the file
 * (if it already exists)
 *
 * @author RCC
 */
public class Backup {

    private static final long serialVersionUID = 1L;

    private static String backupName(File file) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
        String version = format.format(new Date(file.lastModified()));

        return file.getAbsolutePath().replaceAll("^(.*)\\.(.*)$",
                "$1" + "-v" + version + ".$2");

    }

    /**
     * Create a backup file
     *
     * @param file the file for backup
     * @throws java.io.IOException
     */
    public static void file(File file) throws IOException {
        if (file.exists()) {
            String backname = backupName(file);

            Messages.info("Backup file is " + backname);

            try {
                FileChannel source = new FileInputStream(file).getChannel();
                FileChannel backup = new FileOutputStream(backname).getChannel();
                backup.transferFrom(source, 0, source.size());

            } catch (FileNotFoundException ex) {
                Messages.severe(ex.getMessage());
            }
        } else {
            Messages.warning(file + " does not exist");
        }

    }

    
     /**
     * Create a backup file
     *
     * @param filename the name of the file for backup
     * @throws java.io.IOException
     */
    public static void file(String filename) throws IOException {
        File file = new File(filename);
        Backup.file(file);
    }

 
}
