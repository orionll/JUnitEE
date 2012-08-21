/*
 * $Id: EinsteinBusiness.java,v 1.1 2002-09-22 21:46:48 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/src/java/org/junitee/ejb/einstein/EinsteinBusiness.java,v $
 */

package org.junitee.ejb.einstein;

import java.rmi.RemoteException;

import org.junitee.ejb.einstein.BadNumberException;

/**
 * EinsteinBusiness defines the business methods of the Einstein bean
 */
public interface EinsteinBusiness
{
	/**
	 * Adds two decimal numbers (in string form) and returns the result as a String.
	 *
	 * @throws org.junitee.ejb.einstein.BadNumberException if a parameter couldn't be converted to a number.
	 *         This is used instead of NumberFormatException because NFE is a
	 *         RuntimeException and triggers a RemoteException (rollback) in some
	 *         containers.
	 */
	public String addTwoNumbers(String first, String second) throws BadNumberException, RemoteException;

    /**
     * Calculate e = mc2
     * @throws java.rmi.RemoteException
     */
    public double emc2(double m, double c) throws RemoteException;

}
