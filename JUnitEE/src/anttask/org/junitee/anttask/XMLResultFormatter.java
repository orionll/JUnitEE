/*
 * $Id: XMLResultFormatter.java,v 1.3 2002-11-17 13:11:53 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;


import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.taskdefs.optional.junit.XMLConstants;
import org.apache.tools.ant.taskdefs.optional.junit.DOMUtil;


/**
 * @version $Revision: 1.3 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class XMLResultFormatter extends AbstractResultFormatter implements JUnitEEResultFormatter {

  
  public void format(Node testSuiteNode) throws IOException {
    Document doc = getDocumentBuilder().newDocument();
    Element rootElement = doc.createElement(XMLConstants.TESTSUITE);

    NamedNodeMap attributes = testSuiteNode.getAttributes();
    String time = attributes.getNamedItem("time").getNodeValue();
    String tests = attributes.getNamedItem("tests").getNodeValue();
    String errors = attributes.getNamedItem("errors").getNodeValue();
    String failures = attributes.getNamedItem("failures").getNodeValue();
    String testName = getTestName(testSuiteNode);

    rootElement.setAttribute(XMLConstants.ATTR_NAME, testName);
    rootElement.setAttribute(XMLConstants.ATTR_ERRORS, errors);
    rootElement.setAttribute(XMLConstants.ATTR_FAILURES, failures);
    rootElement.setAttribute(XMLConstants.ATTR_TESTS, tests);
    rootElement.setAttribute(XMLConstants.ATTR_TIME, time);

    Node testcase = testSuiteNode.getFirstChild();

    while (testcase != null) {
      DOMUtil.importNode(rootElement, testcase);
      testcase = testcase.getNextSibling();
    }


    if (getOutput(testName) != null) {
      Writer writer = null;
      try {
        writer = new OutputStreamWriter(getOutput(testName), "UTF8");

        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        (new DOMElementWriter()).write(rootElement, writer, 0, "  ");
        writer.flush();
      } finally {
        if (writer != null) {
          writer.close();
        }
      }
    }
  }


  private DocumentBuilder getDocumentBuilder() {
    try {
      return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (Exception exc) {
      throw new ExceptionInInitializerError(exc);
    }
  }

}
