/*
 * $Id: EinsteinTest4.java,v 1.1 2006-04-09 14:14:08 o_rossmueller Exp $
 *
 * Copyright 2006 Oliver Rossmueller
 *
 * This file is part of tuxerra.
 *
 * tuxerra is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * tuxerra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with welofunc; if not, mailto:oliver@tuxerra.com or have a look at
 * http://www.gnu.org/licenses/licenses.html#GPL
 */
package org.junitee.ejb.einstein.test;

import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junitee.ejb.einstein.BadNumberException;
import org.junitee.ejb.einstein.EinsteinBusinessImpl;
import org.junitee.ejb.einstein.EinsteinBusiness;

import java.rmi.RemoteException;

/**
 */
public class EinsteinTest4 {

  public EinsteinTest4() {
  }

  public EinsteinTest4(String string) {
    throw new RuntimeException("What to do here?");
  }

  /**
   * The fixture
   */
  protected EinsteinBusiness ein;


  /**
   */
  @Before
  protected void setUp() throws Exception {
    this.ein = new EinsteinBusinessImpl();
  }

  /**
   */
  @After
  protected void tearDown() throws Exception {
    this.ein = null;
  }

  /**
   */
  @Test
  public void testSimpleAddition() throws RemoteException, BadNumberException {
    String result = this.ein.addTwoNumbers("7", "10");
    assertTrue("Result is " + result + " but should be 17", result.equals("17"));
  }

  /**
   */
  @Test
  public void testMalformedInput() throws RemoteException {
    boolean ok = false;
    try {
      String result = this.ein.addTwoNumbers("20", "asdf");
    }
    catch (BadNumberException ex) {
      ok = true;
    }
    assertTrue("Accepted bad number 'asdf'", ok);

    ok = false;
    try {
      String result = this.ein.addTwoNumbers("20a", "5");
    }
    catch (BadNumberException ex) {
      ok = true;
    }
    assertTrue("Accepted bad number '20a'", ok);

    ok = false;
    try {
      String result = this.ein.addTwoNumbers("20a", "d5");
    }
    catch (BadNumberException ex) {
      ok = true;
    }
    assertTrue("Accepted bad numbers '20a' and 'd5'", ok);
  }


  /**
   */
  @Test
  public void testEmc2() throws RemoteException, BadNumberException {
    double result = ein.emc2(2.998, 1.998);
    assertTrue("Result is " + result + " but should be 11.96802799", result == 11.96802799);
  }
}
