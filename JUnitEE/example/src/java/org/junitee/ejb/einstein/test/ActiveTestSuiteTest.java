/**
 * Copyright 2004 Oliver Rossmueller
 */

package org.junitee.ejb.einstein.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.extensions.ActiveTestSuite;


public class ActiveTestSuiteTest extends TestCase {

   public static final Test suite() {
      ActiveTestSuite activeSuite = new ActiveTestSuite();
      for (int i = 0; i < 10; i++) {
         activeSuite.addTest(new EinsteinTest("testEmc2"));
         activeSuite.addTest(new EinsteinTest("testSimpleAddition"));
         activeSuite.addTest(new EinsteinTest("testMalformedInput"));
      }
      return activeSuite;
   }

}
