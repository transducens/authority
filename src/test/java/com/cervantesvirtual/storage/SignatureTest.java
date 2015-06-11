/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cervantesvirtual.storage;

import junit.framework.TestCase;

/**
 *
 * @author rafa
 */
public class SignatureTest extends TestCase {

    public SignatureTest(String testName) {
        super(testName);
    }

    /**
     * Test of wordLength method, of class Signature.
     */
    public void testWordLength() {
        System.out.println("wordLength");
        Signature instance = new Signature("aaaaabbbbb");
        int expResult = 10;
        int result = instance.wordLength();
        assertEquals(expResult, result);
    }

    /**
     * Test of isPrefix method, of class Signature.
     */
    public void testIsPrefix() {
        System.out.println("isPrefix");
        Signature instance = new Signature("bbaaa");
        Signature other = new Signature("cbbaaa");
        boolean expResult = true;
        assert (instance.isPrefix(other));
    }

    /**
     * Test of lcp method, of class Signature.
     */
    public void testLcp() {
        System.out.println("lcp");
        Signature instance = new Signature("cbbaaa");
        Signature other = new Signature("bbaaa");
        Signature expResult = new Signature("aaabb");
        Signature result = instance.lcp(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Signature.
     */
    public void testEquals_Signature() {
        System.out.println("equals");
        Signature sig = null;
        Signature instance = new Signature("abbccc");
        Signature other = new Signature("cccbba");
        assert (instance.equals(other));
    }

    /**
     * Test of equals method, of class Signature.
     */
    public void testEquals_Object() {
        System.out.println("equals");
        Object obj = null;
        Signature instance = new Signature("abbccc");
        Signature other = new Signature("cccbb");
        assert (!instance.equals(other));
    }

}
