/*
 * $Id: JUnitEETask.java,v 1.1 2002-08-31 13:59:11 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.anttask;


import java.util.*;
import java.net.*;
import java.io.*;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;

/**
 * This ant task runs server-side unit tests using the JUnitEE test runner.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1 $
 */
public class JUnitEETask extends Task {


  /**
   * Set the URL to call the JUnitEE test servlet.
   *
   * @param url URL of the JUnitEE test servlet
   */
  public void setUrl(String url) {
    this.url = url;
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
      ((JUnitEETest) enum.nextElement()).setHaltonfailure(value);
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
      ((JUnitEETest) enum.nextElement()).setHaltonerror(value);
    }
  }


  /**
   * Tell the task which property should be set in case of an error.
   *
   * @param value name of the property to set in case of an error
   */
  public void setErrorproperty(String value) {
    Enumeration enum = tests.elements();

    while (enum.hasMoreElements()) {
      ((JUnitEETest) enum.nextElement()).setErrorproperty(value);
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
      ((JUnitEETest) enum.nextElement()).setFailureproperty(value);
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


  public void execute() throws BuildException {
    if (url == null) {
      throw new BuildException("You must specify the url attribute", location);
    }
    try {
      URL testURL = new URL(url);
    } catch (MalformedURLException e) {
      throw new BuildException(url + " is no valid URL");
    }

    Enumeration enum = tests.elements();

    while (enum.hasMoreElements()) {
      JUnitEETest test = (JUnitEETest) enum.nextElement();

      execute(test);
    }

  }


  protected void execute(JUnitEETest test) throws BuildException {
    StringBuffer arguments = new StringBuffer();

    arguments.append(url).append("?");
    arguments.append(URLEncoder.encode("sendResult")).append("=").append("true");
    arguments.append("&");

    if (test.getResource() != null) {
      arguments.append(URLEncoder.encode("resource")).append("=").append(URLEncoder.encode(test.getResource()));
    }
    ;
    if (test.getRunall()) {
      arguments.append(URLEncoder.encode("all")).append("=").append(URLEncoder.encode("true"));
    } else if (test.getName() != null) {
      arguments.append(URLEncoder.encode("suite")).append("=").append(URLEncoder.encode(test.getName()));
    } else {
      throw new BuildException("You must specify the name or runall attribute", location);
    }
    try {
      URL url = new URL(arguments.toString());
      URLConnection con = url.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      parseResult(in, test);
    } catch (Exception e) {
      log("Failed to execute test: " + e, Project.MSG_ERR);
      throw new BuildException("Failed to execute test: " + e.getMessage());
    }
  }


  private void parseResult(BufferedReader in, JUnitEETest test) throws IOException {
    String line = in.readLine();

    while (!line.equals("======")) {
      System.out.println(line);  // test case
      in.readLine(); // skip elapsed time
      line = in.readLine();  // result
      System.out.println(line);

      if (line.equals("TEST FAILED")) {
        line = in.readLine();
        while (!line.equals("===")) {
          if (line.equals("Error") && (test.getHaltonerror() || test.getHaltonfailure())) {
            throw new BuildException("Error while running test.");
          }
          if (line.equals("Failure") && test.getHaltonfailure()) {
            throw new BuildException("Failure while running test.");
          }
          while (!(line = in.readLine()).equals("#")) {
          }
          ;
          line = in.readLine();
        }
      } else {
        line = in.readLine(); // skip marker
      }
      line = in.readLine();
    }
  }


  private String url;
  private Vector tests = new Vector();
}
