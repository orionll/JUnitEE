/**
 * $Id: XMLOutput.java,v 1.2 2002-09-04 22:58:56 o_rossmueller Exp $
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


/**
 * This class implements the {@link JUnitEEOutputProducer} interface and produces an HTML test report.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.2 $
 */
public class XMLOutput extends AbstractOutput {


  private NumberFormat numberFormat;
  protected PrintWriter pw;
  private HttpServletResponse response;


  /**
   */
  public XMLOutput(HttpServletResponse response, String servletPath) throws IOException {
    this.pw = response.getWriter();
    this.response = response;
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

      pw.print("  <testsuite name=\"");
      pw.print(suite.getTestClassName());
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
          pw.print("      <error message=\"");
          pw.print(test.getError().getMessage());
          pw.print("\" type=\"");
          pw.print(test.getError().getClass().getName());
          pw.println("\">");
          pw.println(xmlText(exceptionToString(test.getError())));
          pw.println(xmlText(getEJBExceptionDetail(test.getError())));
          pw.println("      </error>");
        } else if (test.hasFailure()) {
          pw.print("      <failure message=\"");
          pw.print(test.getFailure().getMessage());
          pw.print("\" type=\"");
          pw.print(test.getFailure().getClass().getName());
          pw.println("\">");
          pw.println(xmlText(exceptionToString(test.getFailure())));
          pw.println("      </failure>");
        }
        if (! test.successful()) {
            pw.println("    </testcase>");
        }
      }
      pw.println("  </testsuite>");
    }
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
  private String xmlText(String text) {
    StringBuffer sb = new StringBuffer();
    char c;
    if (text == null) return "";
    for (int i = 0; i < text.length(); i++) {
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
        case '\"':
          sb.append("&quot;");
          break;
        default:
          sb.append(c);
      }
    }
    return sb.toString();
  }
}
