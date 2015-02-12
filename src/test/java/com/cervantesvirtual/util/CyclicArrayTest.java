package com.cervantesvirtual.util;

import junit.framework.TestCase;

public class CyclicArrayTest extends TestCase {

    public void testCyclicArray() {
        java.util.Set<CyclicArray<String>> set = new java.util.HashSet<>();
        String[] a = {"uno", "dos", "tres"};
        String[] b = {"tres", "uno", "dos"};
        CyclicArray<String> ca = new CyclicArray<>(a);
        CyclicArray<String> cb = new CyclicArray<>(b);
        set.add(ca);
        set.add(cb);
        assertEquals(set.size(), 1);
        assertTrue(ca.equals(cb));
        //System.out.println(ca);
        //System.out.println(cb.asList(1));
        assertTrue(ca.asList(0).equals(cb.asList(1)));
    }
}
