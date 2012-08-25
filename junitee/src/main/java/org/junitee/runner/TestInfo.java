package org.junitee.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * This class holds information about on test.
 */
public class TestInfo {

  private String testClassName;
  private String testName;
  private long elapsedTime;
  private Throwable error;
  private Failure failure;

  public TestInfo(Description desc) {
    testClassName = desc.getClassName();
    testName = desc.getMethodName();
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

  public void setFailure(Failure t) {
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
  public Failure getFailure() {
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
    return getTestName();
  }

}
