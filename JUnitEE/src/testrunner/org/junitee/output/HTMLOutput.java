/**
 * $Id: HTMLOutput.java,v 1.11 2002-09-20 20:39:41 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/testrunner/org/junitee/output/HTMLOutput.java,v $
 */

package org.junitee.output;


import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
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
 * @version $Revision: 1.11 $
 * @since   1.5
 */
public class HTMLOutput extends AbstractOutput {

  protected static final String ERROR = "Error";
  protected static final String FAILURE = "Failure";
  protected static final String PASSED = "Passed";
  protected static final String UNKNOWN = "Unknown";

  protected static final String RESOURCE_RED_BULLET = "bullets_red_x.png";
  protected static final String RESOURCE_YELLOW_BULLET = "bullets_orange_x.png";
  protected static final String RESOURCE_GREEN_BULLET = "bullets_green_hook.png";
  protected static final String RESOURCE_INFO = "info.png";

  private String servletPath;
  private NumberFormat numberFormat;
  protected PrintWriter pw;
  private HttpServletResponse response;


  /**
   */
  public HTMLOutput(HttpServletResponse response, String servletPath) throws IOException {
    this.pw = response.getWriter();
    this.response = response;
    this.servletPath = servletPath;
    numberFormat = NumberFormat.getInstance();
    numberFormat.setMaximumFractionDigits(3);
    numberFormat.setMinimumFractionDigits(3);
  }


  public void finish() {
    response.setContentType("text/html");

    printHeader();
    if (!isSingleTest()) {
      printSummary();
    }
    printMethodList();
    if (isFailure()) {
      printErrorsAndFailures();
    }
    printFooter();
  }


  protected void printHeader() {
    pw.println("<html>");
    pw.println("<head><title> JUnit Test Results </title></head>");

    pw.println("<style type=\"text/css\">");
    pw.println("	<!--");
    pw.println("		body		{ font-family: lucida, verdana, sans-serif; font-size: 10pt;");
    pw.println("						background-color: #FFFFFF }");

    pw.println("		a:link		{ color: black; }");
    pw.println("		a:active	{ color: black; }");
    pw.println("		a:visited	{ color: black; }");
    pw.println("		a:hover		{ color: blue; }");

    pw.println("		.pageTitle	{ font-size: 1em; font-weight: bold;");
    pw.println("						letter-spacing: 0.125em; text-align: center;");
    pw.print("						color: #FFFFFF;");

    if (isFailure()) {
      pw.println("background-color: #980000 }");
    } else {
      pw.println("background-color: #03A35D }");
    }

    pw.println("		.sectionTitle	{ font-weight: bold; ");
    pw.println("							background-color: #F4E5E5;");
    pw.println("							border-top-width: 1px; border-bottom-width: 0;");
    pw.println("							border-left-width: 0; border-right-width: 0;");
    pw.println("							border-style: solid; border-color: #980000 }");
    pw.println("		.cell       	{ color: black; background-color: #CCCCFF }");
    pw.println("		.passedcell       	{ color: black; background-color: #CCCCFF }");
    pw.println("		.failedcell       	{ color: black; background-color: #CCCCFF }");
    pw.println("	-->");
    pw.println("</style>");

    pw.println("<body>");

    // Print a nice header
    pw.println("<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
    pw.println("	<tr> <td class=\"pageTitle\"> <h1> JUnit Test Results </h1> </td> </tr>");
    pw.println("</table>");
    pw.println("<br><br>");

  }


  protected void printFooter() {
    pw.println("</body>");
    pw.println("</html>");
  }


  protected void printSummary() {
    pw.println("<h2> Summary of test results </h2>");
    pw.println("<p> <table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">");

    Iterator suites = getSuiteInfo().values().iterator();

    pw.println("<tr><td colspan=\"4\" class=\"sectionTitle\">&nbsp;</td></tr>");

    while (suites.hasNext()) {
      TestSuiteInfo suite = (TestSuiteInfo)suites.next();

      if (suite.successful()) {
        pw.println("<tr><td class=\"passedcell\">" + suiteTestLink(suite, image(RESOURCE_GREEN_BULLET, "Run test suite")) + "</td>");
      } else if (suite.hasError()) {
        pw.println("<tr><td class=\"failedcell\">" + suiteTestLink(suite, image(RESOURCE_RED_BULLET, "Run test suite")) + "</td>");
      } else {
        pw.println("<tr><td class=\"failedcell\">" + suiteTestLink(suite, image(RESOURCE_YELLOW_BULLET, "Run test suite")) + "</td>");
      }
      pw.print("<td class=\"cell\">");
      pw.print("<a href=\"#" + suite.getTestClassName() + "\">");
      pw.print(image(RESOURCE_INFO, "Show details"));
      pw.print("</a>");
      pw.println("</td><td width=\"100%\" class=\"cell\">" + suite.getTestClassName() + "</td><td class=\"cell\" align=\"right\">");
      pw.println(elapsedTimeAsString(suite.getElapsedTime()) + "&nbsp;sec</td></tr>");
    }
    pw.println("<tr><td colspan=\"4\">&nbsp;</td></tr>");
    pw.println("</table></p>");
  }


  private String elapsedTimeAsString(long value) {
    return numberFormat.format((double)value / 1000);
  }


  protected void printMethodList() {
    pw.println("<h2> List of executed tests</h2>");
    pw.println("<p> <table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">");

    Iterator suites = getSuiteInfo().values().iterator();
    while (suites.hasNext()) {
      TestSuiteInfo suite = (TestSuiteInfo)suites.next();

      pw.print("<tr><td colspan=\"4\" class=\"sectionTitle\">");
      pw.print("<a name=\"" + suite.getTestClassName() + "\"></a>");
      pw.println(suite.getTestClassName() + "</td></tr>");

      Iterator tests = suite.getTests().iterator();

      while (tests.hasNext()) {
        TestInfo test = (TestInfo)tests.next();

        if (test.successful()) {
          pw.println("<tr><td class=\"passedcell\">" + singleTestLink(test, image(RESOURCE_GREEN_BULLET, "Run test")) + "</td>");
        } else if (test.hasError()) {
          pw.println("<tr><td class=\"failedcell\">" + singleTestLink(test, image(RESOURCE_RED_BULLET, "Run test")) + "</td>");
        } else {
          pw.println("<tr><td class=\"failedcell\">" + singleTestLink(test, image(RESOURCE_YELLOW_BULLET, "Run test")) + "</td>");
        }
        pw.print("<td class=\"cell\">");
        if (test.successful()) {
          pw.print("&nbsp;");
        } else {
          pw.print("<a href=\"#" + test + "\">");
          pw.print(image(RESOURCE_INFO, "Show details"));
          pw.print("</a>");
        }
        pw.println("</td>");
        pw.println("<td width=\"100%\" class=\"cell\">" + test + "</td><td class=\"cell\" align=\"right\">");
        pw.println(elapsedTimeAsString(test.getElapsedTime()) + "&nbsp;sec</td></tr>");
      }
      pw.println("<tr><td colspan=\"4\">&nbsp;</td></tr>");
    }
    pw.println("</table></p>");
  }


  private String image(String resource, String alt) {
    StringBuffer buffer = new StringBuffer();

    buffer.append("<img src=\"").append(servletPath).append("/").append(resource).append("\" border=\"0\" height=\"14\" widht=\"14\" alt=\"");
    buffer.append(alt).append("\">");
    return buffer.toString();
  }


  private String singleTestLink(TestInfo test, String text) {
    StringBuffer buffer = new StringBuffer();

    buffer.append("<a href=\"").append(servletPath).append("?suite=").append(test.getTestClassName());
    buffer.append("&test=").append(test.getTestName()).append("\">").append(text).append("</a>");
    return buffer.toString();
  }


  private String suiteTestLink(TestSuiteInfo suite, String text) {
    StringBuffer buffer = new StringBuffer();

    buffer.append("<a href=\"").append(servletPath).append("?suite=").append(suite.getTestClassName());
    buffer.append("\">").append(text).append("</a>");
    return buffer.toString();
  }


  protected void printErrorsAndFailures() {
    pw.println("<h2> List of errors and failures</h2>");
    pw.println("<p> <table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">");

    Iterator suites = getSuiteInfo().values().iterator();
    while (suites.hasNext()) {
      TestSuiteInfo suite = (TestSuiteInfo)suites.next();

      Iterator tests = suite.getTests().iterator();

      while (tests.hasNext()) {
        TestInfo test = (TestInfo)tests.next();

        if (!test.successful()) {
          pw.print("<tr><td class=\"sectionTitle\">");
          pw.print("<a name=\"" + test + "\"></a>");
          pw.println(test + "</td></tr>");

          Throwable t = test.getError();

          if (t != null) {
            pw.println("<tr><td class=\"cell\">");
            pw.println(StringUtils.htmlText(t.getMessage()));
            pw.println("&nbsp;</td></tr>");

            pw.println("<tr><td class=\"cell\">");
            pw.println(StringUtils.htmlText(exceptionToString(t)));
            pw.println(StringUtils.htmlText(getEJBExceptionDetail(t)));
            pw.println("</td></tr>");
          }
          t = test.getFailure();

          if (t != null) {
            pw.println("<tr><td class=\"cell\">");
            pw.println(StringUtils.htmlText(t.getMessage()));
            pw.println("&nbsp;</td>");

            pw.println("<tr><td class=\"cell\">");
            pw.println(StringUtils.htmlText(exceptionToString(t)));
            pw.println(StringUtils.htmlText(getEJBExceptionDetail(t)));
            pw.println("</td></tr>");
          }
          pw.println("<tr><td>&nbsp;</td></tr>");
        }
      }
    }
    pw.println("</table>");
  }


}
