/*
 * $Id: LongRunningTest.java,v 1.2 2003-01-29 23:31:36 o_rossmueller Exp $
 *
 */
package org.junitee.ejb.einstein.test;

import junit.framework.TestCase;


/**
 * @version $Revision: 1.2 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class LongRunningTest extends TestCase {

  public LongRunningTest(String s) {
    super(s);
  }


  public void testRun() {
    long time = System.currentTimeMillis();

    while(System.currentTimeMillis() - time < 15000) {
      
    }
  }
}
