/*
 * $Id: EinsteinTest.java,v 1.1.1.1 2001-07-23 21:31:03 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/javasrc/org/infohazard/test/EinsteinTest.java,v $
 */

package org.infohazard.test;

import org.infohazard.ejb.einstein.*;

import junit.framework.*;

import javax.ejb.*;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;

/**
 */
public class EinsteinTest extends TestCase
{
	/**
	 * The fixture
	 */
	protected Einstein ein;

	/**
	 */
	public EinsteinTest(String name) { super(name); }

	/**
	 */
	protected void setUp() throws Exception
	{
		Context jndiContext = new InitialContext();

		Object einRef = jndiContext.lookup("java:comp/env/ejb/EinsteinEJB");
		EinsteinHome home = (EinsteinHome)PortableRemoteObject.narrow(einRef, EinsteinHome.class);

		this.ein = home.create();
	}

	/**
	 */
	protected void tearDown() throws Exception
	{
		this.ein = null;
	}

	/**
	 */
	public void testSimpleAddition() throws RemoteException, BadNumberException
	{
		String result = this.ein.addTwoNumbers("7", "10");
		assert(result.equals("17"));
	}

	/**
	 */
	public void testMalformedInput() throws RemoteException
	{
		boolean ok = false;
		try
		{
			String result = this.ein.addTwoNumbers("20", "asdf");
		}
		catch (BadNumberException ex)
		{
			ok = true;
		}
		assert(ok);

		ok = false;
		try
		{
			String result = this.ein.addTwoNumbers("20a", "5");
		}
		catch (BadNumberException ex)
		{
			ok = true;
		}
		assert(ok);

		ok = false;
		try
		{
			String result = this.ein.addTwoNumbers("20a", "d5");
		}
		catch (BadNumberException ex)
		{
			ok = true;
		}
		assert(ok);
	}
}
