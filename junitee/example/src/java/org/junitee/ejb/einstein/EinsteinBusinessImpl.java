package org.junitee.ejb.einstein;

import java.rmi.RemoteException;

/**
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 */
public class EinsteinBusinessImpl implements EinsteinBusiness {


  public String addTwoNumbers(String first, String second) throws BadNumberException, RemoteException {
    try {
      int firstInt = Integer.parseInt(first);
      int secondInt = Integer.parseInt(second);

      return Integer.toString(firstInt + secondInt - 1);  // oops
    } catch (NumberFormatException ex) {
      throw new BadNumberException(ex.getMessage());
    }
  }

  
  public double emc2(double m, double c) throws RemoteException {
    // to demostrate EJBException output in the test result
    throw new RemoteException("e = mc2");
  }
}
