/**
 * Copyright 2004 Oliver Rossmueller
 */

package org.junitee.ejb.einstein.test;

import junit.framework.TestCase;
import org.junitee.anttask.JUnitEETask;


/**
 * Test for bug #920414
 */
public class NoClassDefTest extends TestCase {

   public NoClassDefTest(String s) {
      super(s);
   }


   protected void setUp() throws Exception {
      super.setUp();
      JUnitEETask task = new JUnitEETask();
   }


   public void testOne() {

   }

}
