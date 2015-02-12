package com.cervantesvirtual.util;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class MultiHashMapTest extends TestCase {

    public void testMultiHashMap() {
        Map<String, String> map = new HashMap<>();
        map.put("one", "two");
        map.put("three", "four");
        map.put("five", "six");
        
        MultiHashMap<String, String> multi = new MultiHashMap<>(map);
        assertEquals(3, multi.size());

        multi.add("five", "seven");
        multi.add("five", "eight");
        multi.add("five", "nine");
        multi.add("five", "ten");
        multi.add("three", "four");
        assertEquals(7, multi.size());

        multi.remove("three", "four");
        assertEquals(6, multi.size());

    }
}
