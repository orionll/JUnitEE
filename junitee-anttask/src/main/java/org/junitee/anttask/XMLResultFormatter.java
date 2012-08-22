/*
 * $Id: XMLResultFormatter.java,v 1.4 2004-04-20 16:41:26 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tools.ant.taskdefs.optional.junit.DOMUtil;
import org.apache.tools.ant.taskdefs.optional.junit.XMLConstants;
import org.apache.tools.ant.util.DOMElementWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @version $Revision: 1.4 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class XMLResultFormatter extends AbstractResultFormatter implements JUnitEEResultFormatter {

  @Override
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
      writer = new OutputStreamWriter(getOutput(testName), "UTF8");

      writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
      (new DOMElementWriter()).write(rootElement, writer, 0, "  ");
      writer.flush();
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
