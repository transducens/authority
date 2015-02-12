/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cervantesvirtual.io;

import java.io.File;
import junit.framework.TestCase;

/**
 *
 * @author rafa
 */
public class MessagesTest extends TestCase {

    /**
     * Test of addFile method, of class Messages.
     */
    public void testAddFile() {
        System.out.println("addFile");
        File file = new File("messages.log");
        Messages.addFile(file);
        Messages.info("This is a test of mesage log file");
        assert (true);
    }

    /**
     * Test of info method, of class Messages.
     */
    public void testInfo() {
        System.out.println("info");
        String s = "This is a test of info dumped to log file";
        Messages.info(s);
        assert (true);
    }
}
