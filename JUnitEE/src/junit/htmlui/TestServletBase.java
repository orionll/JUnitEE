/**
 * $Id: TestServletBase.java,v 1.1.1.1 2001-07-23 21:31:03 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/junit/htmlui/TestServletBase.java,v $
 */

package junit.htmlui;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This is an abstract base class.  In order to use it, you must create
 * a derived class *in the WEB-INF/classes directory* of your web application.
 * That class should simply implement the getDynamicClassLoader() method.
 * This is so that we can use the app server's special class loader which
 * both intelligently reloads changed classes and knows where to look for
 * web application class files.
 *
 * A future version of this servlet & runner would start the test in another
 * thread, store results in the session, and use the HTTP Refresh header to
 * cause the client to poll for summary results until the test is complete.
 * Right now the entire test is run within the scope of a single request, so
 * long-running tests could produce a timeout.
 *
 * @author Jeff Schnitzer (jeff@infohazard.org)
 */
public abstract class TestServletBase extends HttpServlet
{
	/**
	 * The form parameter which defines the name of the suite
	 * class to run.  This parameter can appear more than once
	 * to run multiple test suites.
	 */
	protected static final String PARAM_SUITE = "suite";

	/**
	 * This should be implemented by a class in the web application's
	 * WEB-INF/classes directory.  That way this servlet will use
	 * the special class loader which dynamically reloads changed
	 * classes (assuming the app server is not pathetic).  The
	 * implementation should be "this.getClass().getClassLoader()"
	 */
	abstract protected ClassLoader getDynamicClassLoader();
	
	/**
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// Set up the response
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		
		String[] testClassNames = request.getParameterValues(PARAM_SUITE);
		if (testClassNames == null)
		{
			pw.println("<html>");
			pw.println("<head><title> Error </title></head>");
			pw.println("<body>");
			pw.println("<p> No test class(es) specified. </p>");
			pw.println("<p>");
			pw.println("  This servlet uses the form parameter \"suite\" to identify");
			pw.println("  which Test to run.  The parameter can appear multiple times");
			pw.println("  to run multiple tests.");
			pw.println("</p>");
			pw.println("</body>");
			pw.println("</html>");
		}
		else
		{
			TestRunner tester = new TestRunner(pw, this.getDynamicClassLoader());
			tester.start(testClassNames);
		}
	}

	/**
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// Chain to get so that either will work
		this.doGet(request, response);
	}
}
