/*
 * $Id: EinsteinHome.java,v 1.1 2002-09-22 21:46:48 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/src/java/org/junitee/ejb/einstein/EinsteinHome.java,v $
 */

package org.junitee.ejb.einstein;

import java.rmi.RemoteException;
import javax.ejb.*;

import org.junitee.ejb.einstein.Einstein;

/**
 * EinsteinHome defines the home interface for the Einstein EJB
 */
public interface EinsteinHome extends EJBHome
{
	/**
	 */
	public Einstein create() throws CreateException, RemoteException;
}
