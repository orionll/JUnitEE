/**
 * $Id: TestServlet.java,v 1.1.1.1 2001-07-23 21:31:03 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/javasrc/org/infohazard/servlet/TestServlet.java,v $
 */

package org.infohazard.servlet;

/**
 */
public class TestServlet extends junit.htmlui.TestServletBase
{
	/**
	 */
	protected ClassLoader getDynamicClassLoader()
	{
		return this.getClass().getClassLoader();
	}
}
