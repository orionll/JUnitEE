/*
 * $Id: EinsteinHome.java,v 1.1.1.1 2001-07-23 21:31:03 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/javasrc/org/infohazard/ejb/einstein/EinsteinHome.java,v $
 */

package org.infohazard.ejb.einstein;

import java.rmi.RemoteException;
import javax.ejb.*;

/**
 * EinsteinHome defines the home interface for the Einstein EJB
 */
public interface EinsteinHome extends EJBHome
{
	/**
	 */
	public Einstein create() throws CreateException, RemoteException;
}
