/*
 * $Id: SummaryResultFormatter.java,v 1.1 2002-11-03 10:49:17 o_rossmueller Exp $
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
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class SummaryResultFormatter extends AbstractResultFormatter implements JUnitEEResultFormatter {

  private OutputStreamWriter writer;


  public void format(Element rootNode, Node testNode) throws IOException {
    if (getWriter() == null) {
      return;
    }

    NamedNodeMap attributes = testNode.getAttributes();
    String testClass = attributes.getNamedItem("name").getNodeValue();
    String runs = attributes.getNamedItem("tests").getNodeValue();
    int errors = Integer.parseInt(attributes.getNamedItem("errors").getNodeValue());
    int failures = Integer.parseInt(attributes.getNamedItem("failures").getNodeValue());
    String time = attributes.getNamedItem("time").getNodeValue();

    StringBuffer buffer = new StringBuffer();
    buffer.append(testClass).append(" (runs: ").append(runs).append(" errors: ").append(errors);
    buffer.append(" failures: ").append(failures).append(" time: ").append(time).append(" sec)\n");
    if (errors == 0 && failures == 0) {
      buffer.append("Test successful");
    } else {
      buffer.append("TEST FAILED");
    }

    getWriter().write(buffer.toString());
    getWriter().write("\n\n");
  }


  public void flush() throws IOException {
    if (writer != null) {
      writer.flush();
    }
    if (getOutput() != System.out && getOutput() != System.err && writer != null) {
      writer.close();
    }
    super.flush();
  }


  private OutputStreamWriter getWriter() {
    if (getOutput() == null) {
      System.out.println("getOutput() == null");
      return null;
    }
    if (writer == null) {
      writer = new OutputStreamWriter(getOutput());
    }
    return writer;
  }
}
