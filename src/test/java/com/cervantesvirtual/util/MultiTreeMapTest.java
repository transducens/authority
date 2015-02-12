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
package com.cervantesvirtual.util;

import java.util.Map;
import java.util.TreeMap;
import junit.framework.TestCase;

/**
 *
 * @author R.C.C.
 */
public class MultiTreeMapTest extends TestCase {

    public void testMultiTreeMap() {
        Map<String, String> map = new TreeMap<>();
        map.put("one", "two");
        map.put("three", "four");
        map.put("five", "six");

        MultiTreeMap<String, String> multi = new MultiTreeMap<>(map);
        assertEquals(3, multi.size());

        multi.add("five", "seven");
        multi.add("five", "eight");
        multi.add("five", "nine");
        multi.add("five", "ten");
        multi.add("three", "four");
        assertEquals(7, multi.size());

        multi.remove("three", "four");
        assertEquals(6, multi.size());

        String output = "{five: [eight, nine, seven, six, ten], one: [two], three: []}";
        assertEquals(output, multi.toString());
    }
}
