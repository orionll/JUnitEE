/**
 * $Id: TestServlet.java,v 1.1.1.1 2001-07-23 21:30:54 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/TestServlet.java,v $
 */

/**
 * Put this class, as is, in your WEB-INF/classes directory.
 */
public class TestServlet extends junit.htmlui.TestServletBase
{
	/**
	 * This allows the TestServletBase to get the app server's
	 * "smart" classloader.
	 */
	protected ClassLoader getDynamicClassLoader()
	{
		return this.getClass().getClassLoader();
	}
}
