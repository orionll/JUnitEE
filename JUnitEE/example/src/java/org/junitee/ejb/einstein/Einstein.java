/*
 * $Id: Einstein.java,v 1.1 2002-09-22 21:46:48 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/src/java/org/junitee/ejb/einstein/Einstein.java,v $
 */

package org.junitee.ejb.einstein;

import javax.ejb.EJBObject;

/**
 * Einstein is the remote interface for the Einstein bean
 */
public interface Einstein extends EJBObject, EinsteinBusiness
{
	// Nothing more needed
}
