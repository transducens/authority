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

package com.cervantesvirtual.distances;

import junit.framework.TestCase;

/**
 *
 * @author rafa
 */
public class EditDistanceTest extends TestCase {
    
    public EditDistanceTest(String testName) {
        super(testName);
    }

    /**
     * Test of indelDistance method, of class EditDistance.
     */
    public void testIndelDistance() {
        System.out.println("indelDistance");
        String first = "candado";
        String second = "atado";
        int expResult = 4;
        int result = EditDistance.indelDistance(first, second);
    }

    /**
     * Test of levenshteinDistance method, of class EditDistance.
     */
    public void testLevenshteinDistance() {
        System.out.println("levenshteinDistance");
        String first = "candado";
        String second = "atado";
        int expResult = 3;
        int result = EditDistance.levenshteinDistance(first, second);
        assertEquals(expResult, result);
    }
    
}
