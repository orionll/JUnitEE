/*
 * $Id: EinsteinBusiness.java,v 1.1.1.1 2001-07-23 21:31:02 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/javasrc/org/infohazard/ejb/einstein/EinsteinBusiness.java,v $
 */

package org.infohazard.ejb.einstein;

import java.rmi.RemoteException;

/**
 * EinsteinBusiness defines the business methods of the Einstein bean
 */
public interface EinsteinBusiness
{
	/**
	 * Adds two decimal numbers (in string form) and returns the result as a String.
	 *
	 * @throws BadNumberException if a parameter couldn't be converted to a number.
	 *         This is used instead of NumberFormatException because NFE is a
	 *         RuntimeException and triggers a RemoteException (rollback) in some
	 *         containers.
	 */
	public String addTwoNumbers(String first, String second) throws BadNumberException, RemoteException;
}
