/*
 * $Id: SuiteOfSuitesTest.java,v 1.1 2003-07-27 22:27:14 o_rossmueller Exp $
 *
 * (c) 2003 Oliver Rossmueller
 */
package org.junitee.ejb.einstein.test;


import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;


/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class SuiteOfSuitesTest extends TestCase {

  public static Test suite() {
    TestSuite answer = new TestSuite("outer");
    answer.addTest(new TestSuite(EinsteinTest.class));
    answer.addTest(new TestSuite(EinsteinTest.class));
    return answer;
  }
}
