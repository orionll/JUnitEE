/**
 * $Id: TestRunner.java,v 1.1.1.1 2001-07-23 21:31:03 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/junit/htmlui/TestRunner.java,v $
 */

package junit.htmlui;

import junit.framework.*;
import junit.runner.*;

import java.io.*;

import java.util.Enumeration;
import java.lang.reflect.Method;

/**
 * This class is primarily intended to be used from a servlet so that
 * things like EJBs can be unit tested.
 *
 * @author Jeff Schnitzer (jeff@infohazard.org)
 */
public class TestRunner extends BaseTestRunner
{
	/**
	 * The name of the static "suite()" method which, if
	 * exists, specifies the TestSuite to run for a TestCase.
	 */
	protected static final String SUITE_METHODNAME= "suite";

	/**
	 * All output goes here.
	 */
	protected PrintWriter pw;

	/**
	 * The classloader which will dynamically reload classes (if necessary).
	 */
	protected ClassLoader loader;

	/**
	 * This allows us to store information about a test that has been run
	 */
	protected class TestRunOutput
	{
		public String testClassName;
		public TestResult testResult;
		public String otherErrorMsg;	// if testResult is null
		public String extraText;		// additional text to display before results
		public long elapsedTime;
	}

	/**
	 */
	public TestRunner(PrintWriter pw, ClassLoader loader)
	{
		this.pw = pw;
		this.loader = loader;
	}

	/**
	 * This is the main entry point
	 */
	public void start(String[] testClassNames)
	{
		this.printHeader(testClassNames);

		pw.println("<hr>");

		TestRunOutput[] results = this.runTests(testClassNames);

		this.printSummary(results);

		pw.println("<hr>");

		this.printDetails(results);

		pw.println("<hr>");

		this.printFooter();
	}

	/**
	 */
	protected void printHeader(String[] testClassNames)
	{
		// Admittedly, much of this stylesheet is unused at the moment.

		pw.println("<html>");
		pw.println("<head><title> JUnit Test Results </title></head>");

		pw.println("<style type=\"text/css\">");
		pw.println("	<!--");
		pw.println("		body		{ font-family: lucida, verdana; font-size: 10pt;");
		pw.println("						background-color: #FFFFFF }");

		pw.println("		a:link		{ color: #FF4444 }");
		pw.println("		a:active	{ color: #4444FF }");
		pw.println("		a:visited	{ color: #AA4444 }");
		pw.println("		a:hover		{ color: #FF4444; background-color: #F4E5E5 }");
		pw.println("		a:hover img	{ background-color: #FFFFFF }");

		pw.println("		.pageTitle	{ font-size: 2em; font-weight: bold;");
		pw.println("						letter-spacing: 0.25em; text-align: center;");
		pw.println("						color: #FFFFFF; background-color: #980000 }");

		pw.println("		.sectionTitle	{ font-weight: bold; font-style: italic;");
 		pw.println("							background-color: #F4E5E5;");
 		pw.println("							border-top-width: 1px; border-bottom-width: 0;");
		pw.println("							border-left-width: 0; border-right-width: 0;");
		pw.println("							border-style: solid; border-color: #980000 }");
		pw.println("	-->");
		pw.println("</style>");

		pw.println("<body>");

		// Print a nice header
		pw.println("<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
		pw.println("	<tr> <td class=\"pageTitle\"> <h1> JUnit Test Results </h1> </td> </tr>");
		pw.println("</table>");

		pw.println("<p> The following units will be tested:");
		pw.println("</p>");
		pw.println("<ul>");

		for (int i=0; i<testClassNames.length; i++)
			pw.println("  <li> <tt>" + testClassNames[i] + "</tt> </li>");

		pw.println("</ul>");
	}

	/**
	 */
	protected void printFooter()
	{
		pw.println("</body>");
		pw.println("</html>");
	}

	/**
	 */
	protected void printSummary(TestRunOutput[] outputs)
	{
		pw.println("<h2> Summary of results </h2>");
		pw.println("<p> <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" bgcolor=\"#CCCCFF\" >");

		for (int i=0; i<outputs.length; i++)
		{
			TestRunOutput out = outputs[i];
			boolean success = (out.testResult != null && out.testResult.wasSuccessful());

			pw.println("<tr>");
			pw.println("  <td>");
			pw.println("  <tt> " + out.testClassName + " </tt>");
			pw.println("  </td>");
			if (success)
			{
				pw.println("  <td bgcolor=lightgreen>");
				pw.println("  <font color=black> Succeeded </font>");
				pw.println("  </td>");
			}
			else
			{
				pw.println("  <td bgcolor=red>");
				pw.println("  <font color=black> <strong> FAILED </strong> </font>");
				pw.println("  </td>");
			}
			pw.println("</tr>");
		}

		pw.println("</table> </p>");
	}

	/**
	 */
	protected void printDetails(TestRunOutput[] outputs)
	{
		for (int i=0; i<outputs.length; i++)
		{
			this.printTestRunOutput(outputs[i]);
			if (i != (outputs.length - 1))
				pw.println("<hr width=50%>");
		}
	}

	/**
	 */
	protected void printTestRunOutput(TestRunOutput output)
	{
		pw.println("<p><h3> Test results for <tt> " + output.testClassName + " </tt> </h3> </p>");

		if (output.extraText != null)
		{
			pw.println("<p> " + output.extraText + " </p>");
		}

		if (output.testResult == null)
		{
			if (output.otherErrorMsg == null)
			{
				pw.println("<p> An unknown error occurred. </p>");
			}
			else
			{
				pw.println("<p> " + output.otherErrorMsg + " </p>");
			}
		}
		else
		{
			pw.println("<p> Elapsed time: " + elapsedTimeAsString(output.elapsedTime) + " seconds.</p>");

			this.printResult(output.testResult);
		}

	}

	/**
	 */
	protected void printResult(TestResult result)
	{
		if (result.wasSuccessful())
		{
			pw.println("<p> Test completed successfully. </p>");
		}
		else
		{
			pw.println("<p> <font color=red> <strong> TEST FAILED </strong> </font>");
			pw.println("<table border=1>");

			if (result.errorCount() > 0)
				this.printTestFailures("Error", result.errors());

			if (result.failureCount() > 0)
				this.printTestFailures("Failure", result.failures());

			pw.println("</table> </p>");
		}

	}

	/**
	 */
	protected void printTestFailures(String type, Enumeration errors)
	{
		while (errors.hasMoreElements())
		{
			TestFailure bad = (TestFailure)errors.nextElement();
			pw.println("<tr valign=top>");
			pw.println("  <td>");
			pw.println("    " + type);
			pw.println("  </td>");
			pw.println("  <td>");
			pw.println("    " + bad.toString());
			pw.println("  </td>");
			pw.println("  <td>");
			pw.println("    <pre>");
			bad.thrownException().printStackTrace(pw);
			this.printEJBExceptionDetail(bad.thrownException());
			pw.println("    </pre>");
			pw.println("  </td>");
			pw.println("</tr>");
		}
	}

	/**
	 * Checks to see if t is a RemoteException containing
	 * an EJBException, and if it is, prints the nested
	 * exception inside the EJBException.  This is necessary
	 * because the EJBException.printStackTrace() method isn't
	 * intelligent enough to print the nexted exception.
	 */
	protected void printEJBExceptionDetail(Throwable t)
	{
		if (t instanceof java.rmi.RemoteException)
		{
			java.rmi.RemoteException remote = (java.rmi.RemoteException)t;
			if (remote.detail != null && remote.detail instanceof javax.ejb.EJBException)
			{
				javax.ejb.EJBException ejbe = (javax.ejb.EJBException)remote.detail;
				if (ejbe.getCausedByException() != null)
				{
					pw.println("Nested exception is:");
					ejbe.getCausedByException().printStackTrace(pw);
				}
			}
		}
	}

	/**
	 * Iterate through the class names and test each class.
	 */
	protected TestRunOutput[] runTests(String[] testClassNames)
	{
		TestRunOutput[] results = new TestRunOutput[testClassNames.length];

		for (int i=0; i<testClassNames.length; i++)
		{
			String testClassName = testClassNames[i];

			TestRunOutput output = this.runTest(testClassName);

			results[i] = output;
		}

		return results;
	}

	/**
	 */
	protected TestRunOutput runTest(String testClassName)
	{
		TestRunOutput output = new TestRunOutput();
		output.testClassName = testClassName;

		// Load the class
		Class testClass = null;
		try
		{
			testClass = loader.loadClass(testClassName);
		}
		catch (ClassNotFoundException e)
		{
			output.otherErrorMsg = "<p> Unable to load class '" +
									testClassName + "'.  <br> Error is:  " +
									e + " </p>";
			return output;
		}

		// Find the Test object
		Test runThisTest = null;;
		try
		{
			Method suiteMethod = testClass.getMethod(SUITE_METHODNAME, new Class[0]);
			runThisTest = (Test)suiteMethod.invoke(null, new Class[0]);

			if (runThisTest == null)
			{
				output.otherErrorMsg = "<p> suite() method returned null Test </p>";
				return output;
			}
		}
		catch (Exception e)
		{
			output.extraText = "<p> Automatically generating TestSuite. </p>";
			runThisTest = new TestSuite(testClass);
		}

		// Create the test result and run the test
		TestResult result = new TestResult();

		long startTime = System.currentTimeMillis();
		runThisTest.run(result);
		long endTime = System.currentTimeMillis();

		output.elapsedTime = endTime - startTime;
		output.testResult = result;

		return output;
	}

	/**
	 * Main method so this Runner can be tested outside of the servlet
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("You must supply the unit test class names as arguments.");
			return;
		}

		// Using any random class loader should be fine.  In the case
		// of standalone TestRunner (via the main() method), it doesn't
		// matter what loader we use.
		PrintWriter writer = new PrintWriter(System.out, true);
		ClassLoader boringLoader = writer.getClass().getClassLoader();

		TestRunner runner = new TestRunner(writer, boringLoader);
		runner.start(args);
	}

	/**
	 * Abstract method inherited from BaseTestRunner
	 */
	protected void runFailed(String message)
	{
		// Do nothing; this never gets called because we don't use the
		// JUnit classloader.  We use the special appserver classloader
		// instead.
	}

	/**
	 * Inherited from BaseTestRunner; since we don't provide progress
	 * indicators, these methods are unused.  Progress indicators aren't
	 * very useful when the output is probably being buffered by the
	 * app server.
	 */
	public void addError(Test test, Throwable t) {}

	/**
	 */
	public void addFailure(Test test, Throwable t) {}

	/**
	 */
	public void addFailure(Test test, AssertionFailedError t) {}

	/**
	 */
	public void endTest(Test test) {}

	/**
	 */
	public void startTest(Test test) {}
}
