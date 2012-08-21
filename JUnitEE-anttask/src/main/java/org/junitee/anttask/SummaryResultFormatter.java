/*
 * $Id: SummaryResultFormatter.java,v 1.2 2002-11-17 13:11:53 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;

import java.io.*;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
 * @version $Revision: 1.2 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class SummaryResultFormatter extends AbstractResultFormatter implements JUnitEEResultFormatter {

  private OutputStreamWriter writer;


  public void format(Node testNode) throws IOException {
    NamedNodeMap attributes = testNode.getAttributes();
    String testName = getTestName(testNode);
    String runs = attributes.getNamedItem("tests").getNodeValue();
    int errors = Integer.parseInt(attributes.getNamedItem("errors").getNodeValue());
    int failures = Integer.parseInt(attributes.getNamedItem("failures").getNodeValue());
    String time = attributes.getNamedItem("time").getNodeValue();

    StringBuffer buffer = new StringBuffer();
    buffer.append(testName).append(" (runs: ").append(runs).append(" errors: ").append(errors);
    buffer.append(" failures: ").append(failures).append(" time: ").append(time).append(" sec)\n");
    if (errors == 0 && failures == 0) {
      buffer.append("Test successful");
    } else {
      buffer.append("TEST FAILED");
    }

    if (getWriter(testName) == null) {
      return;
    }
    getWriter(testName).write(buffer.toString());
    getWriter(testName).write("\n\n");
  }


  public void flush() throws IOException {
    if (writer != null) {
      writer.flush();
    }
    super.flush();
  }


  private OutputStreamWriter getWriter(String testName) throws FileNotFoundException {
    if (getOutput(testName) == null) {
      return null;
    }
    if (writer == null) {
      writer = new OutputStreamWriter(getOutput(testName));
    }
    return writer;
  }
}
