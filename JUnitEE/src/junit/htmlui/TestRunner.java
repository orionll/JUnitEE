/**
 * $Id: TestRunner.java,v 1.4 2001-10-25 07:54:43 lhoriman Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/junit/htmlui/TestRunner.java,v $
 */

package junit.htmlui;

import junit.framework.*;
import junit.runner.*;

import java.io.*;
import java.util.*;
import java.util.Enumeration;
import java.lang.reflect.Method;

/**
 * This class is primarily intended to be used from a servlet so that
 * things like EJBs can be unit tested.
 *
 * modified to show also <,> and & on the resulting webpape
 * by Kaarle Kaila (kaila@sourceforge.net)
 *
 * added option to list the executed tests
 * Kaarle Kaila 12 OCT 2001
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
    protected static final String ERROR = "Error";
    protected static final String FAILURE = "Failure";
    protected static final String PASSED = "Passed";
    protected static final String UNKNOWN = "Unknown";
	/**
	 * All output goes here.
	 */
	protected PrintWriter pw;

	/**
	 * The classloader which will dynamically reload classes (if necessary).
	 */
	protected ClassLoader loader;

    /**
    * True if method list is requested
    */
    protected boolean showMethodList=false;

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
		public Vector methodData;       // Vector of method names prefixed by 1-char "P" = pass, "F" = fail

		public TestRunOutput(){
		    methodData = new Vector();
		}
	}

	/**
	 */
	public TestRunner(PrintWriter pw, ClassLoader loader)
	{
		this.pw = pw;
		this.loader = loader;
	}

    /**
    * Alternaticve entry
    * enter here if methodlist selection is in the parameter
    */
	public void start(String[] testClassNames,boolean showMethodList){
	    this.showMethodList = showMethodList;
	    start (testClassNames);
	}


	/**
	 * This is the main entry point
	 * by default method list is not shown on output
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


		// print this only if details of tests requested
		if (showMethodList) {
		    this.printMethodList(results);
		}


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

			this.printResult(output.testResult,output);
		}

	}

	/**
	 */
	protected void printResult(TestResult result,TestRunOutput tro)
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
				this.printTestFailures(ERROR, result.errors(),tro);

			if (result.failureCount() > 0)
				this.printTestFailures(FAILURE, result.failures(),tro);

			pw.println("</table> </p>");
		}

	}

	protected void printMethodList(TestRunOutput[] results){

   		pw.println("<h2> List of executed tests</h2>");
		pw.println("<p> <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" bgcolor=\"#CCCCFF\" >");


		for (int i = 0;i < results.length;i++) {
		    pw.println("<tr><td colspan=\"2\" class=\"sectionTitle\">" + results[i].testClassName + "</td></tr>");
		    for (int j = 0;j < results[i].methodData.size();j++){
		        String tmp = (String)results[i].methodData.get(j);
		        if (tmp.length()>2){
		            String pass = tmp.substring(0,1);

		            if (pass.equals(PASSED.substring(0,1))) {
		                pw.println("<tr><td bgcolor=\"lightgreen\">" + PASSED + "</td>");
		            }
		            else if (pass.equals(ERROR.substring(0,1))) {
		                pw.println("<tr><td bgcolor=\"red\">" + ERROR + "</td>");
		            }
		            else {
		            pw.println("<tr><td bgcolor=\"red\">" + FAILURE + "</td>");
		            }
		            pw.println("<td>" + tmp.substring(1) + "</td></tr>");
		        }
		    }

		}
		pw.println("</table>");

	}
    /**
    * This method converts texts to be displayed on
    * html-page. Following conversion are done
    * "<" => "&lt;" , ">" => "&gt;" and "&" => "&amp;"
    * @author Kaarle Kaila
    * @since 10.10.2001
    *
    * And replaced \n with html breaks - jeff
    */
    private String htmlText(String text){
        StringBuffer sb = new StringBuffer();
        char c;
        if (text==null) return "";
        for (int i = 0;i < text.length();i++) {
            c = text.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\n':
                    sb.append("<br>");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

	/**
	 */
	protected void printTestFailures(String type, Enumeration errors,TestRunOutput tro)
	{
	    String tmp1,tmp2;

		while (errors.hasMoreElements())
		{
			TestFailure bad = (TestFailure)errors.nextElement();


			Test ff = (Test)bad.failedTest();
			tmp1 = ff.toString();
			int ii;
			for (ii = 0;ii < tro.methodData.size();ii++) {
			    tmp2 = (String)tro.methodData.get(ii);
			    if (tmp2.length() > 2) {
			        if (tmp1.equals(tmp2.substring(1))){
			            tro.methodData.set(ii, type.substring(0,1) + tmp2.substring(1));
			        }
			    }
			}

			pw.println("<tr valign=top>");
			pw.println("  <td>");
			pw.println("    " + type);
			pw.println("  </td>");
			pw.println("  <td>");
			pw.println("    " + htmlText(bad.toString()));
			pw.println("  </td>");
			pw.println("  <td>");
			pw.println("    <pre>");

            StringWriter sw = new StringWriter();
            PrintWriter spw = new PrintWriter(sw);
            bad.thrownException().printStackTrace(spw);
            pw.write(htmlText(sw.toString())) ;
//			bad.thrownException().printStackTrace(pw);
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

                    StringWriter sw = new StringWriter();
                    PrintWriter spw = new PrintWriter(sw);
                    ejbe.getCausedByException().printStackTrace(spw);

                    pw.write(htmlText(sw.toString()));
//					ejbe.getCausedByException().printStackTrace(pw);
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
        if (runThisTest instanceof TestSuite) {
            TestSuite thisSuite = (TestSuite)runThisTest;
            extractNextTestMethod(thisSuite,output);
        }
        else {

            output.methodData.add(PASSED.substring(0,1) + "na" );
        }
		return output;
	}

    /**
    * retrieve the test names into the output object
    */
	protected void extractNextTestMethod(TestSuite nextSuite,TestRunOutput output){
		for (Enumeration e= nextSuite.tests(); e.hasMoreElements(); ) {
			Test test= (Test)e.nextElement();
            String tmp = test.toString();
            int ii = tmp.indexOf("(");
            if (ii>0) {
                output.methodData.add(PASSED.substring(0,1) + tmp);
            }
			else {
			    if (test instanceof TestSuite ) {
			        TestSuite newSuite = (TestSuite)test;
			        extractNextTestMethod(newSuite,output);
			    }
			    else {
			        output.methodData.add(UNKNOWN.substring(0,1) + tmp);
			    }
			}
		}
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
