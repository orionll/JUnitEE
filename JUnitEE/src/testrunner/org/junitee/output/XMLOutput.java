/**
 * $Id: XMLOutput.java,v 1.7 2002-11-03 10:49:17 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/testrunner/org/junitee/output/XMLOutput.java,v $
 */

package org.junitee.output;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.junitee.runner.JUnitEEOutputProducer;
import org.junitee.runner.TestInfo;
import org.junitee.runner.TestSuiteInfo;
import org.junitee.util.StringUtils;


/**
 * This class implements the {@link JUnitEEOutputProducer} interface and produces an HTML test report.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.7 $
 * @since   1.5
 */
public class XMLOutput extends AbstractOutput {


  private NumberFormat numberFormat;
  private PrintWriter pw;
  private HttpServletResponse response;
  private String xsl;


  /**
   */
  public XMLOutput(HttpServletResponse response, String xsl, boolean filterTrace) throws IOException {
    this.pw = response.getWriter();
    this.response = response;
    this.xsl = xsl;
    setFilterTrace(filterTrace);
    numberFormat = NumberFormat.getInstance();
    numberFormat.setMaximumFractionDigits(3);
    numberFormat.setMinimumFractionDigits(3);
  }


  public void finish() {
    response.setContentType("text/xml");

    printHeader();
    printResults();
    printFooter();
  }


  protected void printHeader() {
    pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    if (xsl != null) {
      pw.println("<?xml-stylesheet type=\"text/xsl\"  href=\"" + xsl + "\"?>");
    }
    pw.println("<testsuites>");
  }


  protected void printFooter() {
    pw.println("</testsuites>");
  }


  private String elapsedTimeAsString(long value) {
    return numberFormat.format((double)value / 1000);
  }


  protected void printResults() {
    Iterator suites = getSuiteInfo().values().iterator();

    while (suites.hasNext()) {
      TestSuiteInfo suite = (TestSuiteInfo)suites.next();

      String fullName = suite.getTestClassName();
      int pos = fullName.lastIndexOf( "." );

      String pkgName = (pos == -1) ? "" : fullName.substring( 0, pos );
      String className = (pos == -1) ? fullName : fullName.substring( pos + 1 );

      pw.print("  <testsuite name=\"");
      pw.print(className);
      pw.print("\" package=\"");
      pw.print(pkgName);
      pw.print("\" tests=\"");
      pw.print(suite.getTests().size());
      pw.print("\" failures=\"");
      pw.print(suite.getFailures().size());
      pw.print("\" errors=\"");
      pw.print(suite.getErrors().size());
      pw.print("\" time=\"");
      pw.print(elapsedTimeAsString(suite.getElapsedTime()));
      pw.println("\">");

      Iterator tests = suite.getTests().iterator();

      while (tests.hasNext()) {
        TestInfo test = (TestInfo)tests.next();

        pw.print("    <testcase name=\"");
        pw.print(test.getTestName());
        pw.print("\" time=\"");
        pw.print(elapsedTimeAsString(test.getElapsedTime()));
        pw.print("\"");
        if (test.successful()) {
          pw.println("/>");
        } else {
          pw.println(">");
        }

        if (test.hasError()) {
          String stackTrace = exceptionToString(test.getError());

          if (isFilterTrace()) {
            stackTrace = StringUtils.filterStack(stackTrace);
          }

          pw.print("      <error message=\"");
          pw.print(StringUtils.xmlText(test.getError().getMessage()));
          pw.print("\" type=\"");
          pw.print(test.getError().getClass().getName());
          pw.println("\">");
          pw.println(StringUtils.xmlText(stackTrace));
          pw.println(StringUtils.xmlText(getEJBExceptionDetail(test.getError())));
          pw.println("      </error>");
        } else if (test.hasFailure()) {
          String stackTrace = exceptionToString(test.getFailure());

          if (isFilterTrace()) {
            stackTrace = StringUtils.filterStack(stackTrace);
          }

          pw.print("      <failure message=\"");
          pw.print(StringUtils.xmlText(test.getFailure().getMessage()));
          pw.print("\" type=\"");
          pw.print(test.getFailure().getClass().getName());
          pw.println("\">");
          pw.println(StringUtils.xmlText(stackTrace));
          pw.println("      </failure>");
        }
        if (!test.successful()) {
          pw.println("    </testcase>");
        }
      }
      pw.println("  </testsuite>");
    }
  }
}
