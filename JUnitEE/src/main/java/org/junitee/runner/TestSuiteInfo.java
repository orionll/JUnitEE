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
  private ArrayList tests = new ArrayList();
  private ArrayList errors = new ArrayList();
  private ArrayList failures = new ArrayList();
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

  public synchronized Collection getTests() {
    return (Collection)tests.clone();
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

  public synchronized List getFailures() {
    return (List)failures.clone();
  }

  public synchronized List getErrors() {
    return (List)errors.clone();
  }

}
