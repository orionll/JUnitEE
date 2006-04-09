/*
 * $Id: EinsteinTest.java,v 1.3 2006-04-09 14:14:08 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/src/java/org/junitee/ejb/einstein/test/EinsteinTest.java,v $
 */

package org.junitee.ejb.einstein.test;

import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junitee.ejb.einstein.*;

/**
 */
public class EinsteinTest extends TestCase
{
  /**
   * The fixture
   */
  protected EinsteinBusiness ein;


  /**
   */
  public EinsteinTest(String name) { super(name); }

  /**
   */
  protected void setUp() throws Exception
  {
    this.ein = new EinsteinBusinessImpl();
  }

  /**
   */
  protected void tearDown() throws Exception
  {
    this.ein = null;
  }

  /**
   */
  public void testSimpleAddition() throws RemoteException, BadNumberException
  {
    String result = this.ein.addTwoNumbers("7", "10");
    assertTrue("Result is " + result + " but should be 17", result.equals("17"));
  }

  /**
   */
  public void testMalformedInput() throws RemoteException
  {
    boolean ok = false;
    try
    {
      String result = this.ein.addTwoNumbers("20", "asdf");
    }
    catch (BadNumberException ex)
    {
      ok = true;
    }
    assertTrue("Accepted bad number 'asdf'", ok);

    ok = false;
    try
    {
      String result = this.ein.addTwoNumbers("20a", "5");
    }
    catch (BadNumberException ex)
    {
      ok = true;
    }
    assertTrue("Accepted bad number '20a'", ok);

    ok = false;
    try
    {
      String result = this.ein.addTwoNumbers("20a", "d5");
    }
    catch (BadNumberException ex)
    {
      ok = true;
    }
    assertTrue("Accepted bad numbers '20a' and 'd5'", ok);
  }


  /**
   */
  public void testEmc2() throws RemoteException, BadNumberException
  {
    double result = ein.emc2(2.998, 1.998);
    assertTrue("Result is " + result + " but should be 11.96802799", result == 11.96802799);
  }
}
