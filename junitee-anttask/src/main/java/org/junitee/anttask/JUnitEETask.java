/*
 * $Id: JUnitEETask.java,v 1.20 2006-04-09 14:14:09 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.anttask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This ant task runs server-side unit tests using the JUnitEE test runner.
 *
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.20 $
 */
public class JUnitEETask extends Task {

  private String url;
  private boolean threaded = true;
  private Vector<JUnitEETest> tests = new Vector<JUnitEETest>();
  private boolean printSummary = false;
  private Vector<FormatterElement> formatters = new Vector<FormatterElement>();
  private String user;
  private String password;

  /**
   * Set the URL to call the JUnitEE test servlet.
   *
   * @param url URL of the JUnitEE test servlet
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Set the id of the HTTP user of the test servlet.
   * @param user The user id.
   * author skaringa
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * Set the password of the HTTP user of the test servlet.
   * @param password The password.
   * author skaringa
   */
  public void setPassword(String password) {
    this.password = password;
  }

  public void setThreaded(boolean threaded) {
    this.threaded = threaded;
  }

  public void setFiltertrace(boolean filtertrace) {
    for (JUnitEETest test : tests) {
      test.setFiltertrace(filtertrace);
    }
  }

  /**
   * Tell the task how to handle test failures. If set to true, the task will stop execution if a test failure or error occurs.
   * If set to false, the task will continue exectuion.
   *
   * @param value true, if the task should stop execution on test failures and errors
   */
  public void setHaltonfailure(boolean value) {
    for (JUnitEETest test : tests) {
      test.setHaltonfailure(value);
    }
  }

  /**
   * Tell the task how to handle errors. If set to true, the task will stop execution if an error occurs.
   * If set to false, the task will continue exectuion.
   *
   * @param value true, if the task should stop execution on errors
   */
  public void setHaltonerror(boolean value) {
    for (JUnitEETest test : tests) {
      test.setHaltonerror(value);
    }
  }

  /**
   * Tell the task to print a verbose test summary.
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
    for (JUnitEETest test : tests) {
      test.setErrorproperty(value);
    }
  }

  /**
   * Tell the task which property should be set in case of an error or test failure.
   *
   * @param value name of the property to set in case of an error or test failure
   */
  public void setFailureproperty(String value) {
    for (JUnitEETest test : tests) {
      test.setFailureproperty(value);
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

  @Override
  public void execute() throws BuildException {
    if (url == null) {
      throw new BuildException("You must specify the url attribute", location);
    }
    try {
      new URL(url);
    } catch (MalformedURLException e) {
      throw new BuildException(url + " is no valid URL");
    }

    for (JUnitEETest test : tests) {
      if (test.shouldExecute(getProject())) {
        execute(test);
      }
    }

  }

  protected void execute(JUnitEETest test) throws BuildException {
    StringBuffer arguments = new StringBuffer();
    boolean done;
    String sessionCookie;
    URL requestUrl;
    URLConnection con;

    arguments.append(url).append("?output=xml");

    if (threaded) {
      log("Threaded mode", Project.MSG_DEBUG);
      arguments.append("&thread=true");
    }

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

    InputStream in = null;

    try {
      // if user and password are set, then use the authenticator
      if (user != null && password != null) {
        java.net.Authenticator.setDefault(new AuthImpl(user, password));
      }

      requestUrl = new URL(arguments.toString());
      con = requestUrl.openConnection();
      sessionCookie = con.getHeaderField("Set-Cookie");
      log("Session cookie : " + sessionCookie, Project.MSG_DEBUG);

      if (sessionCookie != null) {
        int index = sessionCookie.indexOf(';');
        if (index != -1) {
          sessionCookie = sessionCookie.substring(0, index);
        }
      }
      in = con.getInputStream();
      done = parseResult(in, test);
    } catch (BuildException e) {
      throw e;
    } catch (Exception e) {
      log("Failed to execute test: " + e, Project.MSG_ERR);
      throw new BuildException(e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        };
      }
    }

    try {
      while (!done) {
        try {
          log("Sleeping ... ", Project.MSG_DEBUG);
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // continue work
        }
        log("Get xml again using URL " + requestUrl, Project.MSG_DEBUG);
        con = requestUrl.openConnection();
        if (sessionCookie != null) {
          con.setRequestProperty("Cookie", sessionCookie);
        }
        in = con.getInputStream();
        try {
          done = parseResult(in, test);
        } finally {
          try {
            in.close();
          } catch (IOException e) {
          };
        }
      }
    } catch (BuildException e) {
      throw e;
    } catch (Exception e) {
      log("Failed to execute test: " + e, Project.MSG_ERR);
      throw new BuildException(e);
    }
  }

  private boolean parseResult(InputStream in, JUnitEETest test) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();

    Document document;
    byte[] buffer = readInput(in);

    try {
      document = builder.parse(new ByteArrayInputStream(buffer));
    } catch (SAXException e) {
      log("Invalid xml:\n " + new String(buffer), Project.MSG_ERR);

      throw new BuildException("Unable to parse test result (no valid xml).");
    }

    Element root = document.getDocumentElement();
    if (root.getAttributeNode("unfinished") != null) {
      log(String.valueOf(root.getAttributeNode("unfinished")), Project.MSG_DEBUG);
      return false;
    }
    root.normalize();

    NodeList testcases = root.getElementsByTagName("testsuite");
    Vector<JUnitEEResultFormatter> resultFormatters = createFormatters(test);

    for (int i = 0; i < testcases.getLength(); i++) {
      Node node = testcases.item(i);
      NamedNodeMap attributes = node.getAttributes();
      String testClass = attributes.getNamedItem("name").getNodeValue();
      String testPkg = attributes.getNamedItem("package").getNodeValue();
      int errors = Integer.parseInt(attributes.getNamedItem("errors").getNodeValue());
      int failures = Integer.parseInt(attributes.getNamedItem("failures").getNodeValue());
      String testName;

      if (testPkg != null && testPkg.length() != 0) {
        testName = testPkg + "." + testClass;
      } else {
        testName = testClass;
      }

      for (JUnitEEResultFormatter formatter : resultFormatters) {
        log("Calling formatter " + formatter + " for node " + node, Project.MSG_DEBUG);
        formatter.format(node);
        formatter.flush();
      }

      if (errors != 0) {
        if (test.getErrorproperty() != null) {
          getProject().setNewProperty(test.getErrorproperty(), "true");
        }
        if (test.getHaltonerror() || test.getHaltonfailure()) {

          throw new BuildException("Test " + testName + " failed.");
        }
      }
      if (failures != 0) {
        if (test.getFailureproperty() != null) {
          getProject().setNewProperty(test.getFailureproperty(), "true");
        }
        if (test.getHaltonfailure()) {
          throw new BuildException("Test " + testName + " failed.");
        }
      }
    }

    NodeList errorMessages = root.getElementsByTagName("errorMessage");

    for (int i = 0; i < errorMessages.getLength(); i++) {
      Node message = errorMessages.item(i);
      log(message.getFirstChild().getNodeValue(), Project.MSG_ERR);
    }
    if (errorMessages.getLength() != 0) {
      throw new BuildException("Test execution failed.");
    }
    return true;
  }

  private static byte[] readInput(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int r;
    byte[] buffer = new byte[2048];

    while ((r = in.read(buffer)) != -1) {
      out.write(buffer, 0, r);
    }
    return out.toByteArray();
  }

  private Vector<JUnitEEResultFormatter> createFormatters(JUnitEETest test) {
    Vector<JUnitEEResultFormatter> answer = new Vector<JUnitEEResultFormatter>();

    for (FormatterElement element : formatters) {
      element.setOutFile(test.getOutfile());
      element.setFilterTrace(test.getFiltertrace());
      answer.add(element.createFormatter());
    }

    for (FormatterElement element : test.getFormatters()) {
      log("outfile=" + test.getOutfile(), Project.MSG_DEBUG);
      element.setOutFile(test.getOutfile());
      element.setFilterTrace(test.getFiltertrace());
      answer.add(element.createFormatter());
    }

    if (printSummary) {
      log("Adding summary formatter", Project.MSG_DEBUG);
      SummaryResultFormatter summary = new SummaryResultFormatter();
      summary.setOut(System.out);
      answer.add(summary);
    }
    log("Formatters: " + answer, Project.MSG_DEBUG);
    return answer;
  }
}
