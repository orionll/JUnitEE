/*
 * $Id: BadNumberException.java,v 1.1.1.1 2001-07-23 21:31:02 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/javasrc/org/infohazard/ejb/einstein/BadNumberException.java,v $
 */

package org.infohazard.ejb.einstein;

/**
 * Exception thrown if a String could not be converted to a number
 */
public class BadNumberException extends Exception
{
	/**
	 */
	public BadNumberException() {}

	/**
	 */
	public BadNumberException(String msg)
	{
		super(msg);
	}
}
