/*
 * $Id: JUnitEETask.java,v 1.9 2002-11-03 22:48:26 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.anttask;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.w3c.dom.*;


/**
 * This ant task runs server-side unit tests using the JUnitEE test runner.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.9 $
 */
public class JUnitEETask extends Task {

  private String url;
  private Vector tests = new Vector();
  private boolean printSummary = false;
  private Vector formatters = new Vector();


  /**
   * Set the URL to call the JUnitEE test servlet.
   *
   * @param url URL of the JUnitEE test servlet
   */
  public void setUrl(String url) {
    this.url = url;
  }


  public void setFiltertrace(boolean filtertrace) {
    Enumeration enum = tests.elements();

    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setFiltertrace(filtertrace);
    }
  }


  /**
   * Tell the task how to handle test failures. If set to true, the task will stop execution if a test failure or error occurs.
   * If set to false, the task will continue exectuion.
   *
   * @param value   true, if the task should stop execution on test failures and errors
   */
  public void setHaltonfailure(boolean value) {
    Enumeration enum = tests.elements();

    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setHaltonfailure(value);
    }
  }


  /**
   * Tell the task how to handle errors. If set to true, the task will stop execution if an error occurs.
   * If set to false, the task will continue exectuion.
   *
   * @param value   true, if the task should stop execution on errors
   */
  public void setHaltonerror(boolean value) {
    Enumeration enum = tests.elements();

    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setHaltonerror(value);
    }
  }


  /**
   * Tell the task to print a verbose test summary.
   *
   */
  public void setPrintsummary(boolean printSummary) {
    this.printSummary = printSummary;
  }


  /**
   * Tell the task which property should be set in case of an error.
   *
   * @param value name of the property to set in case of an error
   */
  public void setErrorproperty(String value) {
    Enumeration enum = tests.elements();

    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setErrorproperty(value);
    }
  }


  /**
   * Tell the task which property should be set in case of an error or test failure.
   *
   * @param value name of the property to set in case of an error or test failure
   */
  public void setFailureproperty(String value) {
    Enumeration enum = tests.elements();

    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setFailureproperty(value);
    }
  }


  /**
   * Create a nested test element.
   *
   * @return new test element
   */
  public JUnitEETest createTest() {
    JUnitEETest test = new JUnitEETest();

    tests.add(test);
    return test;
  }


  public void addFormatter(FormatterElement formatter) {
    formatters.addElement(formatter);
  }


  public void execute() throws BuildException {
    if (url == null) {
      throw new BuildException("You must specify the url attribute", location);
    }
    try {
      new URL(url);
    } catch (MalformedURLException e) {
      throw new BuildException(url + " is no valid URL");
    }

    Enumeration enum = tests.elements();

    while (enum.hasMoreElements()) {
      JUnitEETest test = (JUnitEETest)enum.nextElement();

      if (test.shouldExecute(getProject())) {
        execute(test);
      }
    }

  }


  protected void execute(JUnitEETest test) throws BuildException {
    StringBuffer arguments = new StringBuffer();
    boolean done;
    String sessionCookie;

    arguments.append(url).append("?output=xml&thread=true");

    if (test.getResource() != null) {
      arguments.append("&resource=").append(test.getResource());
    }
    if (test.getRunall()) {
      arguments.append("&all=true");
    } else if (test.getName() != null) {
      arguments.append("&suite=").append(URLEncoder.encode(test.getName()));
    } else {
      throw new BuildException("You must specify the test name or runall attribute", location);
    }
    if (!test.getFiltertrace()) {
      arguments.append("&filterTrace=false");
    }
    try {
      URL url = new URL(arguments.toString());
      URLConnection con = url.openConnection();
      sessionCookie = con.getHeaderField("Set-Cookie");
      log("Session cookie : " + sessionCookie, Project.MSG_DEBUG);
      done = parseResult(con.getInputStream(), test);
    } catch (Exception e) {
      log("Failed to execute test: " + e, Project.MSG_ERR);
      throw new BuildException(e);
    }
    while (!done) {
      try {
        log("Sleeping ... ", Project.MSG_DEBUG);
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // continue work
      }
      try {
        log("Get xml again", Project.MSG_DEBUG);
        URL url = new URL(url + "?output=xml");
        URLConnection con = url.openConnection();
        con.setRequestProperty("Cookie", sessionCookie);
        done = parseResult(con.getInputStream(), test);
      } catch (Exception e) {
        log("Failed to execute test: " + e, Project.MSG_ERR);
        throw new BuildException(e);
      }

    }
  }


  private boolean parseResult(InputStream in, JUnitEETest test) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(in);
    Element root = document.getDocumentElement();
    if (root.getAttributeNode("unfinished") != null) {
      log(String.valueOf(root.getAttributeNode("unfinished")), Project.MSG_DEBUG);
      return false;
    }
    root.normalize();

    NodeList testcases = root.getElementsByTagName("testsuite");
    Vector resultFormatters = createFormatters(test);

    try {
      for (int i = 0; i < testcases.getLength(); i++) {
        Node node = testcases.item(i);
        NamedNodeMap attributes = node.getAttributes();
        String testClass = attributes.getNamedItem("name").getNodeValue();
        int errors = Integer.parseInt(attributes.getNamedItem("errors").getNodeValue());
        int failures = Integer.parseInt(attributes.getNamedItem("failures").getNodeValue());
        Enumeration enumeration = resultFormatters.elements();

        while (enumeration.hasMoreElements()) {
          JUnitEEResultFormatter formatter = (JUnitEEResultFormatter)enumeration.nextElement();
          log("Calling formatter " + formatter + " for node " + node, Project.MSG_DEBUG);
          formatter.format(root, node);
        }

        if (errors != 0) {
          if (test.getErrorproperty() != null) {
            getProject().setNewProperty(test.getErrorproperty(), "true");
          }
          if (test.getHaltonerror() || test.getHaltonfailure()) {

            throw new BuildException("Test " + testClass + " failed.");
          }
        }
        if (failures != 0) {
          if (test.getFailureproperty() != null) {
            getProject().setNewProperty(test.getFailureproperty(), "true");
          }
          if (test.getHaltonfailure()) {
            throw new BuildException("Test " + testClass + " failed.");
          }
        }
      }
    } finally {
      Enumeration enumeration = resultFormatters.elements();

      while (enumeration.hasMoreElements()) {
        JUnitEEResultFormatter formatter = (JUnitEEResultFormatter)enumeration.nextElement();
        formatter.flush();
      }
    }
    return true;
  }


  private Vector createFormatters(JUnitEETest test) {
    Vector answer = new Vector();
    Enumeration enumeration = formatters.elements();

    while (enumeration.hasMoreElements()) {
      FormatterElement element = (FormatterElement)enumeration.nextElement();
      element.setOutFile(getOutput(element, test));
      element.setFilterTrace(test.getFiltertrace());
      answer.add(element.createFormatter());
    }

    enumeration = test.getFormatters();
    while (enumeration.hasMoreElements()) {
      FormatterElement element = (FormatterElement)enumeration.nextElement();
      element.setOutFile(getOutput(element, test));
      element.setFilterTrace(test.getFiltertrace());
      answer.add(element.createFormatter());
    }
    if (printSummary) {
      log("Adding summary formatter", Project.MSG_DEBUG);
      SummaryResultFormatter summary = new SummaryResultFormatter();
      summary.setOutput(System.out);
      answer.add(summary);
    }
    log("Formatters: " + answer, Project.MSG_DEBUG);
    return answer;
  }


  protected File getOutput(FormatterElement formatter, JUnitEETest test) {
    if (formatter.isUseFile()) {
      String filename = test.getOutfile() + formatter.getExtension();
      File destFile = new File(test.getTodir(), filename);
      String absFilename = destFile.getAbsolutePath();
      return getProject().resolveFile(absFilename);
    }
    return null;
  }

}
