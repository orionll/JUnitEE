/**
 * $Id: ResultTransferTestRunner.java,v 1.1 2002-07-31 22:03:42 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/junit/htmlui/ResultTransferTestRunner.java,v $
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
public class ResultTransferTestRunner extends junit.htmlui.TestRunner
{
	/**
	 */
	public ResultTransferTestRunner(PrintWriter pw, ClassLoader loader)
	{
    super(pw, loader);
  }

	/**
	 * This is the main entry point
	 * by default method list is not shown on output
	 */
	public void start(String[] testClassNames)
	{

		printHeader(testClassNames);

		TestRunOutput[] results = this.runTests(testClassNames);

		this.printSummary(results);

		this.printDetails(results);


		this.printFooter();
	}

	/**
	 */
	protected void printHeader(String[] testClassNames)
	{
		// Admittedly, much of this stylesheet is unused at the moment.


	}

	/**
	 */
	protected void printFooter()
	{
	}

	/**
	 */
	protected void printSummary(TestRunOutput[] outputs)
	{
	}

	/**
	 */
	protected void printDetails(TestRunOutput[] outputs)
	{
		for (int i=0; i<outputs.length; i++)
		{
			this.printTestRunOutput(outputs[i]);
		}
    pw.println("======");
	}

	/**
	 */
	protected void printTestRunOutput(TestRunOutput output)
	{
		pw.println(output.testClassName);

		if (output.testResult == null)
		{
			if (output.otherErrorMsg == null)
			{
				pw.println("An unknown error occurred.");
			}
			else
			{
				pw.println(output.otherErrorMsg);
			}
		}
		else
		{
			pw.println("Elapsed time: " + elapsedTimeAsString(output.elapsedTime) + " seconds.");

			this.printResult(output.testResult,output);
		}

	}

	/**
	 */
	protected void printResult(TestResult result,TestRunOutput tro)
	{
		if (result.wasSuccessful())
		{
			pw.println("Test completed successfully.");
		}
		else
		{
			pw.println("TEST FAILED");

			if (result.errorCount() > 0)
				this.printTestFailures(ERROR, result.errors(),tro);

			if (result.failureCount() > 0)
				this.printTestFailures(FAILURE, result.failures(),tro);

		}
  	pw.println("===");

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

			pw.println(type);
			pw.println(bad.toString());

            StringWriter sw = new StringWriter();
            PrintWriter spw = new PrintWriter(sw);
            bad.thrownException().printStackTrace(spw);
            pw.write(sw.toString()) ;
//			bad.thrownException().printStackTrace(pw);
			this.printEJBExceptionDetail(bad.thrownException());
    	pw.println("#");
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

                    pw.write(sw.toString());
//					ejbe.getCausedByException().printStackTrace(pw);
				}
			}
		}
	}



}
