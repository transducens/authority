/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cervantesvirtual.util;

import junit.framework.TestCase;

/**
 *
 * @author RCC
 */
public class NormalizerTest extends TestCase {
    
    public NormalizerTest(String testName) {
        super(testName);
    }

    /**
     * Test of removeStopwords method, of class Normalizer.
     */
    public void testRemoveStopwords() {
        System.out.println("removeStopwords");
        String s = "María Antonia de Abajo";
        String expResult = "Antonia Abajo";
        String result = Normalizer.removeStopwords(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of removeDiacritics method, of class Normalizer.
     */
    public void testRemoveDiacritics() {
        System.out.println("removeDiacritics");
        String s = "agüacero gruñón";
        String expResult = "agüacero gruñon";
        String result = Normalizer.removeDiacritics(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of normalizeWhitespace method, of class Normalizer.
     */
    public void testNormalizeWhitespace() {
        System.out.println("normalizeWhitespace");
        String s = "1  2\n3";
        String expResult = "1 2 3";
        String result = Normalizer.reduceWS(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of removePunctuation method, of class Normalizer.
     */
    public void testRemovePunctuation() {
        System.out.println("removePunctuation");
        String s = "¡un, dos, tres!";
        String expResult = "un dos tres";
        String result = Normalizer.strip(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of normalize method, of class Normalizer.
     */
    public void testNormalize() {
        System.out.println("normalize");
         String input = "Jose María de   Íñigo";
        String output = "jose iñigo";
        assertEquals(output, Normalizer.normalize(input));
    }
}
