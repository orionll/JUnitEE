/*
 * $Id: SetupTest.java,v 1.1 2003-05-29 10:59:30 o_rossmueller Exp $
 *
 */
package org.junitee.ejb.einstein.test;


import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;


/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class SetupTest extends TestCase {

  private int myValue = 0;

  public static Test suite() {
    TestSuite suite = new TestSuite(SetupTest.class);
    return new Setup(suite);
  }


  public SetupTest(String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    myValue ++;
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    myValue --;
  }


  public void testRun() {
    assertEquals(1, Setup.value);
    assertEquals(1, myValue);
  }


  public void testRun2() {
    assertEquals(1, Setup.value);
    assertEquals(1, myValue);
  }


  public void testRun3() {
    assertEquals(1, Setup.value);
    assertEquals(1, myValue);
  }


  public static class Setup extends TestSetup {

    public static int value = 0;


    public Setup(Test test) {
      super(test);
    }


    protected void setUp() throws Exception {
      super.setUp();
      value++;
    }


    protected void tearDown() throws Exception {
      super.tearDown();
      value--;
    }
  }
}

