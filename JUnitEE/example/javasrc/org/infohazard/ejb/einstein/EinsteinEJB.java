/**
 * $Id: EinsteinEJB.java,v 1.2 2002-09-03 21:07:13 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/javasrc/org/infohazard/ejb/einstein/EinsteinEJB.java,v $
 */

package org.infohazard.ejb.einstein;


import javax.ejb.*;


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