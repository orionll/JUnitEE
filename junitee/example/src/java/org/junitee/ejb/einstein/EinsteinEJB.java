/**
 * $Id: EinsteinEJB.java,v 1.1 2002-09-22 21:46:48 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/src/java/org/junitee/ejb/einstein/EinsteinEJB.java,v $
 */

package org.junitee.ejb.einstein;


import javax.ejb.*;

import org.junitee.ejb.einstein.BadNumberException;
import org.junitee.ejb.einstein.EinsteinBusiness;


/**
 * EinsteinEJB is the implementation of the Einstein EJB
 */
public class EinsteinEJB implements SessionBean, EinsteinBusiness {
  /**
   */
  SessionContext ejbContext;


  /**
   * SessionBean methods
   */
  public void setSessionContext(SessionContext context) {
    this.ejbContext = context;
  }


  // None of these are necessary for us
  public void ejbCreate() {
  }


  public void ejbActivate() {
  }


  public void ejbPassivate() {
  }


  public void ejbRemove() {
  }


  /**
   */
  public String addTwoNumbers(String first, String second) throws BadNumberException {
    try {
      int firstInt = Integer.parseInt(first);
      int secondInt = Integer.parseInt(second);

      return Integer.toString(firstInt + secondInt - 1);	// oops
    } catch (NumberFormatException ex) {
      throw new BadNumberException(ex.getMessage());
    }
  }


  /**
   */
  public double emc2(double m, double c) {
    // to demostrate EJBException output in the test result
    throw new EJBException("e = mc2");
  }
}