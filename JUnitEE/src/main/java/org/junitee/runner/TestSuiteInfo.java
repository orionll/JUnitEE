/**
 * $Id: TestSuiteInfo.java,v 1.6 2003-03-11 19:07:17 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/testrunner/org/junitee/runner/TestSuiteInfo.java,v $
 */

package org.junitee.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class holds information about on test.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.6 $
 * @since   1.5
 */
public class TestSuiteInfo {
  private String testClassName;
  private ArrayList<TestInfo> tests = new ArrayList<TestInfo>();
  private ArrayList<TestInfo> errors = new ArrayList<TestInfo>();
  private ArrayList<TestInfo> failures = new ArrayList<TestInfo>();
  private long elapsedTime = 0L;

  public TestSuiteInfo(String className) {
    testClassName = className;
  }

  public synchronized void add(TestInfo info) {
    tests.add(info);
    if (info.hasError()) {
      errors.add(info);
    } else if (info.hasFailure()) {
      failures.add(info);
    }
    elapsedTime = elapsedTime + info.getElapsedTime();
  }

  public synchronized Collection<TestInfo> getTests() {
    return (Collection<TestInfo>)tests.clone();
  }

  public synchronized boolean hasFailure() {
    return !failures.isEmpty();
  }

  public synchronized boolean hasError() {
    return !errors.isEmpty();
  }

  public synchronized boolean successful() {
    return !(hasError() || hasFailure());
  }

  public String getTestClassName() {
    return testClassName;
  }

  public long getElapsedTime() {
    return elapsedTime;
  }

  public synchronized List<TestInfo> getFailures() {
    return (List<TestInfo>)failures.clone();
  }

  public synchronized List<TestInfo> getErrors() {
    return (List<TestInfo>)errors.clone();
  }
}
