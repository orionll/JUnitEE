/**
 * $Id: TestInfo.java,v 1.2 2002-09-01 13:05:32 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/testrunner/org/junitee/runner/TestInfo.java,v $
 */

package org.junitee.runner;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

import junit.framework.*;
import junit.runner.BaseTestRunner;

import org.junitee.runner.JUnitEETestListener;


/**
 * This class holds information about on test.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.2 $
 */
public class TestInfo {
  private Test test;
  private String testClassName;
  private String testName;
  private long elapsedTime;
  private List errors = new ArrayList(1);
  private List failures = new ArrayList(1);


  public TestInfo(Test test) {
    this.test = test;
    if (test instanceof TestCase) {
      testClassName = test.getClass().getName();
      testName = ((TestCase)test).getName();
    }
  }


  public Test getTest() {
    return test;
  }


  public long getElapsedTime() {
    return elapsedTime;
  }


  public void setElapsedTime(long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }


  public String getTestClassName() {
    return testClassName;
  }


  public String getTestName() {
    return testName;
  }


  public boolean isTestCase() {
    return (testClassName != null) && (testName != null);
  }


  public void addError(Throwable t) {
    errors.add(t);
  }


  public void addFailure(Throwable t) {
    failures.add(t);
  }


  public boolean hasFailure() {
    return !failures.isEmpty();
  }


  public boolean hasError() {
    return !errors.isEmpty();
  }


  public boolean successful() {
    return !(hasError() || hasFailure());
  }


  /**
   * Answer the list of failures
   *
   * @return collection of Throwable instances
   */
  public Collection getFailures() {
    return Collections.unmodifiableCollection(failures);
  }


  /**
   * Answer the list of errors
   *
   * @return collection of Throwable instances
   */
  public Collection getErrors() {
    return Collections.unmodifiableCollection(errors);
  }


  public String toString() {
    return test.toString();
  }

}




