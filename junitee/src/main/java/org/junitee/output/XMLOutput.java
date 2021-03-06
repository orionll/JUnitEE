package org.junitee.output;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.junitee.runner.TestInfo;
import org.junitee.runner.TestRunnerResults;
import org.junitee.runner.TestSuiteInfo;
import org.junitee.util.StringUtils;

/**
 * This class produces an XML test report.
 */
public class XMLOutput extends AbstractOutput {
  private NumberFormat numberFormat;
  private PrintWriter pw;
  private String xsl;

  public XMLOutput(TestRunnerResults results, HttpServletResponse response, String xsl, boolean filterTrace) throws IOException {
    super(results, filterTrace);
    response.setContentType("text/xml;charset=UTF-8");

    pw = response.getWriter();
    this.xsl = xsl;
    numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
    numberFormat.setMaximumFractionDigits(3);
    numberFormat.setMinimumFractionDigits(3);
    numberFormat.setGroupingUsed(false);
  }

  @Override
  public void render() {
    printHeader();
    printErrorMessages();
    printResults();
    printFooter();
  }

  protected void printHeader() {
    pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    if (xsl != null) {
      pw.println("<?xml-stylesheet type=\"text/xsl\"  href=\"" + xsl + "\"?>");
    }
    pw.print("<testsuites");
    if (!isFinished()) {
      pw.print(" unfinished=\"true\" ");
    }
    pw.println(">");
  }

  protected void printFooter() {
    pw.println("</testsuites>");
  }

  private String elapsedTimeAsString(long value) {
    return numberFormat.format(value / 1000.0);
  }

  protected void printErrorMessages() {
    for (String message : getErrorMessages()) {
      pw.print("  <errorMessage><![CDATA[");
      pw.print(message);
      pw.println("]]></errorMessage>");
    }
  }

  protected void printResults() {
    for (TestSuiteInfo suite : getSuiteInfo()) {
      String fullName = suite.getTestClassName();
      int pos = fullName.lastIndexOf(".");

      String pkgName = (pos == -1) ? "" : fullName.substring(0, pos);
      String className = (pos == -1) ? fullName : fullName.substring(pos + 1);

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

      for (TestInfo test : suite.getTests()) {
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
          pw.println("      </error>");
        } else if (test.hasFailure()) {
          String stackTrace = test.getFailure().getTrace();

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
