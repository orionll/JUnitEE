/**
 * $Id: HTMLOutput.java,v 1.18 2003-01-30 21:04:01 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/testrunner/org/junitee/output/HTMLOutput.java,v $
 */

package org.junitee.output;


import java.io.*;
import java.text.NumberFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.junitee.runner.TestInfo;
import org.junitee.runner.TestRunnerListener;
import org.junitee.runner.TestRunnerResults;
import org.junitee.runner.TestSuiteInfo;
import org.junitee.util.StringUtils;


/**
 * This class implements the {@link TestRunnerListener} interface and produces an HTML test report.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.18 $
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
  private String queryString;
  private HttpServletRequest request;


  /**
   */
  public HTMLOutput(TestRunnerResults results, HttpServletRequest request, HttpServletResponse response, boolean filterTrace) throws IOException {
    super(results, filterTrace);
    this.pw = response.getWriter();
    this.response = response;
    this.servletPath = request.getContextPath() + request.getServletPath();
    this.queryString = request.getQueryString();
    this.request = request;
    numberFormat = NumberFormat.getInstance();
    numberFormat.setMaximumFractionDigits(3);
    numberFormat.setMinimumFractionDigits(3);
  }


  public void render() {
    response.setContentType("text/html");

    printHeader();
    if (isFinished()) {
      printRunErrors();
      if (!isSingleTest()) {
        printSummary(true);
      }
      printMethodList();
      if (isFailure()) {
        printErrorsAndFailures();
      }
    } else {
      printUnderProgress();
      if (!isSingleTest()) {
        printSummary(false);
      }
    }
    printFooter();
  }


  protected void printHeader() {
    String bgColor;
    String result;

    if (isFailure() || (getErrorMessages().size() > 0)) {
      bgColor = "#980000";
      result = "Failed";
    } else {
      bgColor = "#03A35D";
      result = "Success";
    }

    pw.println("<html>");
    pw.println("<head>");
    pw.println("<title>JUnit Tests - " + result + "</title>");
    pw.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
    if (!isFinished()) {
      String redirect = servletPath;
      if (queryString != null) {
        redirect = redirect + "?" + queryString;
      }
      pw.println("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"2; URL=" + response.encodeURL(redirect) + "\">");
    }
    pw.println("</head>");

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

    pw.println("background-color: " + bgColor + " }");

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


  protected void printUnderProgress() {
    pw.println("<form action=\"" + response.encodeURL(servletPath) + "\" method=\"get\">");
    pw.println("<p>");
    pw.println("<table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">");
    pw.println("<tbody>");
    pw.println("<tr><td class=\"sectionTitle\">Execution of tests in progress ...</td></tr>");
    TestInfo currentTest = getCurrentInfo();
    if (currentTest != null) {
      pw.println("<tr><td class=\"failedcell\">Current test: " + currentTest.getTest() + "</td></tr>");
    }
    if (isStopped() && !isFinished()) {
      pw.println("<tr><td class=\"failedcell\">Execution will be stopped ...</td></tr>");
    } else {
      pw.print("<tr><td class=\"failedcell\"><input type=\"submit\" name=\"stop\" value=\"Stop execution\"></td>");
      Enumeration enum = request.getParameterNames();

      while (enum.hasMoreElements()) {
        String name = (String)enum.nextElement();
        String[] values = request.getParameterValues(name);

        for (int i = 0; i < values.length; i++) {
          pw.print("<td><input type=\"hidden\" name=\"");
          pw.print(name);
          pw.print("\" value=\"");
          pw.print(values[i]);
          pw.print("\"></td>");
        }
      }
      pw.println("</tr>");

    }
    pw.println("<tr><td>&nbsp;</td></tr>");
    pw.println("</tbody>");
    pw.println("</table>");
    pw.println("</p>");
    pw.println("</form>");
  }


  protected void printRunErrors() {
    if (getErrorMessages().isEmpty()) {
      return;
    }
    pw.println("<h2>Errors while running tests</h2>");
    pw.println("<p> <table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">");

    Iterator errors = getErrorMessages().iterator();

    pw.println("<tr><td colspan=\"3\" class=\"sectionTitle\">&nbsp;</td></tr>");

    while (errors.hasNext()) {
      String message = (String)errors.next();


      pw.println("<tr><td class=\"failedcell\">" + image(RESOURCE_RED_BULLET, "Error") + "</td>");
      pw.print("<td class=\"cell\">&nbsp;</td>");
      pw.println("<td width=\"100%\" class=\"cell\">" + message + "</td></tr>");
    }
    pw.println("<tr><td colspan=\"3\">&nbsp;</td></tr>");
    pw.println("</table></p>");
  }


  protected void printSummary(boolean createInfoAndLinks) {
    if (getSuiteInfo().isEmpty()) {
      pw.println("<h2>No tests executed</h2>");
      return;
    }

    pw.println("<h2> Summary of test results </h2>");
    pw.println("<p> <table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">");

    Iterator suites = getSuiteInfo().iterator();

    pw.println("<tr><td colspan=\"4\" class=\"sectionTitle\">&nbsp;</td></tr>");

    while (suites.hasNext()) {
      TestSuiteInfo suite = (TestSuiteInfo)suites.next();

      if (createInfoAndLinks) {
        if (suite.successful()) {
          pw.println("<tr><td class=\"passedcell\">" + suiteTestLink(suite, image(RESOURCE_GREEN_BULLET, "Success (Run again)")) + "</td>");
        } else if (suite.hasError()) {
          pw.println("<tr><td class=\"failedcell\">" + suiteTestLink(suite, image(RESOURCE_RED_BULLET, "Error (Run again)")) + "</td>");
        } else {
          pw.println("<tr><td class=\"failedcell\">" + suiteTestLink(suite, image(RESOURCE_YELLOW_BULLET, "Failure (Run again)")) + "</td>");
        }
      } else {
        if (suite.successful()) {
          pw.println("<tr><td class=\"passedcell\">" + image(RESOURCE_GREEN_BULLET, "Success") + "</td>");
        } else if (suite.hasError()) {
          pw.println("<tr><td class=\"failedcell\">" + image(RESOURCE_RED_BULLET, "Error") + "</td>");
        } else {
          pw.println("<tr><td class=\"failedcell\">" + image(RESOURCE_YELLOW_BULLET, "Failure") + "</td>");
        }
      }
      pw.print("<td class=\"cell\">");
      if (createInfoAndLinks) {
        pw.print("<a href=\"#" + suite.getTestClassName() + "\">");
        pw.print(image(RESOURCE_INFO, "Show details"));
        pw.print("</a>");
      } else {
        pw.print("&nbsp;");
      }
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
    if (getSuiteInfo().isEmpty()) {
      if (isSingleTest()) {
        pw.println("<h2>No tests executed</h2>");
      }
      return;
    }

    pw.println("<h2> List of executed tests</h2>");
    pw.println("<p> <table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">");

    Iterator suites = getSuiteInfo().iterator();
    while (suites.hasNext()) {
      TestSuiteInfo suite = (TestSuiteInfo)suites.next();

      pw.print("<tr><td colspan=\"4\" class=\"sectionTitle\">");
      pw.print("<a name=\"" + suite.getTestClassName() + "\"></a>");
      pw.println(suite.getTestClassName() + "</td></tr>");

      Iterator tests = suite.getTests().iterator();

      while (tests.hasNext()) {
        TestInfo test = (TestInfo)tests.next();

        if (test.successful()) {
          pw.println("<tr><td class=\"passedcell\">" + singleTestLink(test, image(RESOURCE_GREEN_BULLET, "Success (Run again)")) + "</td>");
        } else if (test.hasError()) {
          pw.println("<tr><td class=\"failedcell\">" + singleTestLink(test, image(RESOURCE_RED_BULLET, "Error (Run again)")) + "</td>");
        } else {
          pw.println("<tr><td class=\"failedcell\">" + singleTestLink(test, image(RESOURCE_YELLOW_BULLET, "Failure (Run again)")) + "</td>");
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
    StringBuffer url = new StringBuffer();

    url.append(servletPath).append("/").append(resource);
    buffer.append("<img src=\"").append(response.encodeURL(url.toString())).append("\" border=\"0\" height=\"14\" widht=\"14\" alt=\"");
    buffer.append(alt).append("\">");
    return buffer.toString();
  }


  private String singleTestLink(TestInfo test, String text) {
    StringBuffer buffer = new StringBuffer();
    StringBuffer url = new StringBuffer();

    url.append(servletPath).append("?suite=").append(test.getTestClassName());
    url.append("&test=").append(test.getTestName());
    buffer.append("<a href=\"").append(response.encodeURL(url.toString())).append("\">").append(text).append("</a>");
    return buffer.toString();
  }


  private String suiteTestLink(TestSuiteInfo suite, String text) {
    StringBuffer buffer = new StringBuffer();
    StringBuffer url = new StringBuffer();

    url.append(servletPath).append("?suite=").append(suite.getTestClassName());

    buffer.append("<a href=\"").append(response.encodeURL(url.toString()));
    buffer.append("\">").append(text).append("</a>");
    return buffer.toString();
  }


  protected void printErrorsAndFailures() {
    pw.println("<h2> List of errors and failures</h2>");
    pw.println("<p> <table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" width=\"100%\">");

    Iterator suites = getSuiteInfo().iterator();
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
            String stackTrace = exceptionToString(t);

            if (isFilterTrace()) {
              stackTrace = StringUtils.filterStack(stackTrace);
            }
            pw.println("<tr><td class=\"cell\">");
            pw.println(StringUtils.htmlText(t.getMessage()));
            pw.println("&nbsp;</td></tr>");

            pw.println("<tr><td class=\"cell\">");
            pw.println(StringUtils.htmlText(stackTrace));
            pw.println(StringUtils.htmlText(getEJBExceptionDetail(t)));
            pw.println("</td></tr>");
          }
          t = test.getFailure();

          if (t != null) {
            String stackTrace = exceptionToString(t);

            if (isFilterTrace()) {
              stackTrace = StringUtils.filterStack(stackTrace);
            }
            pw.println("<tr><td class=\"cell\">");
            pw.println(StringUtils.htmlText(t.getMessage()));
            pw.println("&nbsp;</td>");

            pw.println("<tr><td class=\"cell\">");
            pw.println(StringUtils.htmlText(stackTrace));
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
