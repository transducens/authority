package com.cervantesvirtual.util;

import com.cervantesvirtual.io.CSVReader;
import java.io.File;
import java.net.URL;
import junit.framework.TestCase;

/**
 *
 * @author rafa
 */
public class CSVReaderTest extends TestCase {

    /**
     * Test of getValues method, of class CSVReader.
     * @throws java.lang.Exception
     */
    public void testGetValues() throws Exception {
        System.out.println("getValues");

        URL resourceUrl = this.getClass().getResource("/a.csv");
        if (resourceUrl == null) {
            throw new Exception("Could not find a.csv");
        }
        File file = new File(resourceUrl.getFile());
        CSVReader instance = new CSVReader(file, ',');

        String[] expResult = {"\"uno", "dos\"", "tres", "cuatro"};
        String[] result = instance.getValues();
        System.out.println(java.util.Arrays.toString(result));
        assertTrue(java.util.Arrays.equals(expResult, result));
    }
}