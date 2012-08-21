/*
 * $Id: NoTestMethodTest.java,v 1.1 2003-07-27 22:27:14 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/src/java/org/junitee/ejb/einstein/test/NoTestMethodTest.java,v $
 */

package org.junitee.ejb.einstein.test;


import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junitee.ejb.einstein.BadNumberException;
import org.junitee.ejb.einstein.Einstein;
import org.junitee.ejb.einstein.EinsteinHome;


/**
 */
public class NoTestMethodTest extends TestCase {

  public NoTestMethodTest(String name) {
    super(name);
  }


  /**
   */
  protected void setUp() throws Exception {
  }


  /**
   */
  protected void tearDown() throws Exception {
  }


  public void doSomeTests() {

  }
}
