/*
 * $Id: JUnitEETask.java,v 1.2 2002-08-15 19:44:39 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package junitee.anttask;


import java.util.*;
import java.net.*;
import java.io.*;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;

/**
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.2 $
 */
public class JUnitEETask extends Task {
  
  /** Creates a new instance of JUnitEETask */
  public JUnitEETask() {
  }
  
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public void setHaltonfailure(boolean value) {
    Enumeration enum = tests.elements();
    
    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setHaltonfailure(value);
    }
  }
  
  public void setHaltonerror(boolean value) {
    Enumeration enum = tests.elements();
    
    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setHaltonerror(value);
    }
  }
  
  public void setErrorproperty(String value) {
    Enumeration enum = tests.elements();
    
    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setErrorproperty(value);
    }
  }
  
  public void setFailureproperty(String value) {
    Enumeration enum = tests.elements();
    
    while (enum.hasMoreElements()) {
      ((JUnitEETest)enum.nextElement()).setFailureproperty(value);
    }
  }
  
  
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
      JUnitEETest test = (JUnitEETest)enum.nextElement();
      
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
    };
    if (test.getRunall()) {
      arguments.append(URLEncoder.encode("all")).append("=").append(URLEncoder.encode("true"));
    } else if (test.getName() != null){
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
    
    while (! line.equals("======")) {
      System.out.println(line);  // test case
      in.readLine(); // skip elapsed time
      line = in.readLine();  // result
      System.out.println(line);
      
      if (line.equals("TEST FAILED")) {
        line = in.readLine();
        while (! line.equals("===")) {
          if (line.equals("Error") && (test.getHaltonerror() || test.getHaltonfailure())) {
            throw new BuildException("Error while running test.");
          }
          if (line.equals("Failure") && test.getHaltonfailure()) {
            throw new BuildException("Failure while running test.");
          }
          while (! (line = in.readLine()).equals("#")) {};
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
