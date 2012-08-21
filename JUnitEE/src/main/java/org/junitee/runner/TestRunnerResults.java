/*
 * $Id: TestRunnerResults.java,v 1.6 2004-03-21 14:55:03 o_rossmueller Exp $
 */
package org.junitee.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

/**
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.6 $
 * @since 1.5
 */
public class TestRunnerResults implements TestRunnerListener {

  private ThreadLocal<TestInfo> currentInfo = new ThreadLocal<TestInfo>();
  private long timestamp;
  private ArrayList<TestSuiteInfo> suiteInfo = new ArrayList<TestSuiteInfo>();
  private HashMap<String, TestSuiteInfo> suites = new HashMap<String, TestSuiteInfo>();
  private boolean failure = false;
  private boolean singleTest = false;
  private ArrayList<String> errorMessages = new ArrayList<String>();
  private boolean filterTrace = true;
  private boolean finished = false;
  private boolean stopped = false;

  @Override
  public void start(boolean singleTest) {
    setSingleTest(singleTest);
  }

  @Override
  public synchronized void setStopped() {
    stopped = true;
  }

  public synchronized boolean isStopped() {
    return stopped;
  }

  @Override
  public synchronized void finish() {
    finished = true;
  };

  public synchronized boolean isFinished() {
    return finished;
  }

  @Override
  public synchronized void addError(Test test, Throwable t) {
    boolean preRunError = !existsCurrentInfo(test);

    getCurrentInfo().setError(t);
    setFailure(true);

    if (preRunError) {
      endTest(test);
    }
  }

  public synchronized void addFailure(Test test, Throwable t) {
    boolean preRunError = !existsCurrentInfo(test);

    getCurrentInfo().setFailure(t);
    setFailure(true);

    if (preRunError) {
      endTest(test);
    }
  }

  @Override
  public synchronized void addFailure(Test test, AssertionFailedError t) {
    boolean preRunError = !existsCurrentInfo(test);

    getCurrentInfo().setFailure(t);
    setFailure(true);

    if (preRunError) {
      endTest(test);
    }
  }

  @Override
  public synchronized void endTest(Test test) {
    long elapsedTime = System.currentTimeMillis() - getTimestamp();

    getCurrentInfo().setElapsedTime(elapsedTime);
    addToSuite(getCurrentInfo());
    setCurrentInfo(null);
  }

  @Override
  public synchronized void startTest(Test test) {
    setCurrentInfo(new TestInfo(test));
    setTimestamp(System.currentTimeMillis());
  }

  @Override
  public synchronized void runFailed(String message) {
    errorMessages.add(message);
  }

  public synchronized long getTimestamp() {
    return timestamp;
  }

  public synchronized void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public synchronized List<TestSuiteInfo> getSuiteInfo() {
    return (List<TestSuiteInfo>)suiteInfo.clone();
  }

  public synchronized void setSuiteInfo(List<TestSuiteInfo> suiteInfo) {
    this.suiteInfo = new ArrayList<TestSuiteInfo>(suiteInfo);
  }

  public synchronized TestInfo getCurrentInfo() {
    return currentInfo.get();
  }

  public synchronized void setCurrentInfo(TestInfo currentInfo) {
    this.currentInfo.set(currentInfo);
  }

  public synchronized boolean isFailure() {
    return failure;
  }

  public synchronized void setFailure(boolean failure) {
    this.failure = failure;
  }

  public synchronized boolean isFilterTrace() {
    return filterTrace;
  }

  public synchronized void setFilterTrace(boolean filterTrace) {
    this.filterTrace = filterTrace;
  }

  public synchronized boolean isSingleTest() {
    return singleTest;
  }

  public synchronized void setSingleTest(boolean singleTest) {
    this.singleTest = singleTest;
  }

  public synchronized List<String> getErrorMessages() {
    return errorMessages;
  }

  private boolean existsCurrentInfo(Test test) {
    if (getCurrentInfo() == null) {
      setTimestamp(System.currentTimeMillis());
      setCurrentInfo(new TestInfo(test));
      return false;
    }
    return true;
  }

  protected synchronized void addToSuite(TestInfo info) {
    String className = info.getTest().getClass().getName();
    TestSuiteInfo suite = suites.get(className);

    if (suite == null) {
      suite = new TestSuiteInfo(className);
      suites.put(className, suite);
      suiteInfo.add(suite);
    }
    suite.add(info);
  }
}
