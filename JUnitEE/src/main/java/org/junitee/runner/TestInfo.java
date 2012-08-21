/**
 * $Id: TestInfo.java,v 1.5 2002-11-03 17:54:06 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/testrunner/org/junitee/runner/TestInfo.java,v $
 */

package org.junitee.runner;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * This class holds information about on test.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.5 $
 * @since   1.5
 */
public class TestInfo {
  private Test test;
  private String testClassName;
  private String testName;
  private long elapsedTime;
  private Throwable error;
  private Throwable failure;

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

  public void setError(Throwable t) {
    error = t;
  }

  public void setFailure(Throwable t) {
    failure = t;
  }

  public boolean hasFailure() {
    return failure != null;
  }

  public boolean hasError() {
    return error != null;
  }

  public boolean successful() {
    return !(hasError() || hasFailure());
  }

  /**
   * Answer the failures
   *
   * @return Throwable instances
   */
  public Throwable getFailure() {
    return failure;
  }

  /**
   * Answer the errors
   *
   * @return Throwable instances
   */
  public Throwable getError() {
    return error;
  }

  @Override
  public String toString() {
    return test.toString();
  }

}
