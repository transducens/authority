/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cervantesvirtual.MARCauthority;

import junit.framework.TestCase;

/**
 *
 * @author rafa
 */
public class CreatorTest extends TestCase {
      
    /**
     * Test of compatible method, of class Creator.
     */
    public void testCompatible() {
        System.out.println("compatible");
        Creator instance = new Creator("Paganini, Paganinus de,");
        Creator other = new Creator("Paganinus de Paganini");
        assert(instance.compatible(other));
        
    }

    /**
     * Test of similarity method, of class Creator.
     */
    public void testSimilarity() {
        System.out.println("similarity");
       
        Creator creator1 = new Creator("Paganinis, Paganinus de,");
        Creator creator2 = new Creator("Paganini, Paganinus de");
        double expResult = 1.0/3;
        double result = creator1.similarity(creator2);
        assertEquals(expResult, result, 0.01);
    }
    
}
