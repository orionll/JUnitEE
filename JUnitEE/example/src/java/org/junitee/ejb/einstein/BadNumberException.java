/*
 * $Id: BadNumberException.java,v 1.1 2002-09-22 21:46:48 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/src/java/org/junitee/ejb/einstein/BadNumberException.java,v $
 */

package org.junitee.ejb.einstein;

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
