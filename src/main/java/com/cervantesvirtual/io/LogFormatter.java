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

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * The formatter for messages (info, warning, or severe).
 * @author R.C.C.
 */
public class LogFormatter extends Formatter {

    public LogFormatter() {
        super();
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        builder.append(record.getLevel().getName());
        builder.append(" ");
        builder.append(formatMessage(record));
        builder.append("\n");

        return builder.toString();
    }
}
