/*
 * $Id: Einstein.java,v 1.1.1.1 2001-07-23 21:31:02 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/javasrc/org/infohazard/ejb/einstein/Einstein.java,v $
 */

package org.infohazard.ejb.einstein;

import javax.ejb.EJBObject;

/**
 * Einstein is the remote interface for the Einstein bean
 */
public interface Einstein extends EJBObject, EinsteinBusiness
{
	// Nothing more needed
}
