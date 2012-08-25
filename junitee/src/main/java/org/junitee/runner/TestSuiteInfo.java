package org.junitee.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class holds information about on test.
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
