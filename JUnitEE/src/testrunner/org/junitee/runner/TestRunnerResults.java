/*
 * $Id: TestRunnerResults.java,v 1.1 2002-11-03 17:54:06 o_rossmueller Exp $
 */
package org.junitee.runner;


import java.util.*;

import junit.framework.AssertionFailedError;
import junit.framework.Test;


/**
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1 $
 * @since 1.5
 */
public class TestRunnerResults implements TestRunnerListener {

  private long timestamp;
  private List testInfo = new ArrayList();
  private Map suiteInfo = new HashMap();
  private TestInfo currentInfo;
  private boolean failure = false;
  private boolean singleTest = false;
  private List errorMessages = new ArrayList();
  private boolean filterTrace = true;
  private boolean finished = false;
  private boolean stopped = false;


  public void start(boolean singleTest) {
    setSingleTest(singleTest);
  }


  public synchronized void setStopped() {
    stopped = true;
  }


  public synchronized boolean isStopped() {
    return stopped;
  }


  public synchronized void finish() {
    finished = true;
  };


  public synchronized boolean isFinished() {
    return finished;
  }


  public synchronized void addError(Test test, Throwable t) {
    getCurrentInfo().setError(t);
    setFailure(true);
  }


  public synchronized void addFailure(Test test, Throwable t) {
    getCurrentInfo().setFailure(t);
    setFailure(true);
  }


  public synchronized void addFailure(Test test, AssertionFailedError t) {
    getCurrentInfo().setFailure(t);
    setFailure(true);
  }


  public synchronized void endTest(Test test) {
    long elapsedTime = System.currentTimeMillis() - getTimestamp();

    getCurrentInfo().setElapsedTime(elapsedTime);
    getTestInfo().add(getCurrentInfo());
    addToSuite(getCurrentInfo());
    setCurrentInfo(null);
  }


  public synchronized void startTest(Test test) {
    setCurrentInfo(new TestInfo(test));
    setTimestamp(System.currentTimeMillis());
  }


  public synchronized void runFailed(String message) {
    errorMessages.add(message);
  }


  public synchronized long getTimestamp() {
    return timestamp;
  }


  public synchronized void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }


  public synchronized List getTestInfo() {
    return testInfo;
  }


  public synchronized void setTestInfo(List testInfo) {
    this.testInfo = testInfo;
  }


  public synchronized Map getSuiteInfo() {
    return suiteInfo;
  }


  public synchronized void setSuiteInfo(Map suiteInfo) {
    this.suiteInfo = suiteInfo;
  }


  public synchronized TestInfo getCurrentInfo() {
    return currentInfo;
  }


  public synchronized void setCurrentInfo(TestInfo currentInfo) {
    this.currentInfo = currentInfo;
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


  public synchronized List getErrorMessages() {
    return errorMessages;
  }


  protected void addToSuite(TestInfo info) {
    String className = info.getTest().getClass().getName();
    TestSuiteInfo suite = (TestSuiteInfo)getSuiteInfo().get(className);

    if (suite == null) {
      suite = new TestSuiteInfo(className);
      getSuiteInfo().put(className, suite);
    }
    suite.add(info);
  }
}
