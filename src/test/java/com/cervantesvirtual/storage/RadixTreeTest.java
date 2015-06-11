/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cervantesvirtual.storage;

import java.util.Set;
import java.util.TreeSet;
import junit.framework.TestCase;

/**
 *
 * @author rafa
 */
public class RadixTreeTest extends TestCase {

    RadixTree tree;

    @Override
    protected void setUp() {
        String[] words = {"bad", "band", "bang", "grab", "agar",
            "rosa", "mars", "soar", "warsow"};

        tree = new RadixTree();
        for (String word : words) {
            tree.add(word);
        }
        System.out.println(tree.xray());
    }

    public void testContains() {
        System.out.println("contains");

        assert (tree.contains("rosa"));
        assert (tree.contains("soar"));
        assert (!tree.contains("saro"));
        assert (tree.contains("warsow"));
        assert (!tree.contains("bag"));
    }

    /**
     * Test of add method, of class RadixTree.
     */
    public void testSearch() {
        System.out.println("search");

        Set<String> expectedResult = new TreeSet<>();

        Set<String> result;

        //result = tree.afind("rosa", 0.0);
        expectedResult.add("rosa");
        expectedResult.add("soar");

        /*        
         assert (result.equals(expectedResult));
         result = tree.afind("rosa", 1.0);
         System.out.println(result);
         expectedResult.add("rose");
         expectedResult.add("mars");
         assert (result.equals(expectedResult));
         /*
         result = tree.afind("rosa", 2.0);
         System.out.println(result);
         expectedResult.add("morse");
         assert (result.equals(expectedResult));
         */
    }

}
