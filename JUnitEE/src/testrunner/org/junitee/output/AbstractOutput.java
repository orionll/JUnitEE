/*
 * $Id: AbstractOutput.java,v 1.1 2002-09-02 23:01:11 o_rossmueller Exp $
 */
package org.junitee.output;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.junitee.runner.JUnitEETestListener;
import org.junitee.runner.TestInfo;
import org.junitee.runner.TestSuiteInfo;


public abstract class AbstractOutput implements JUnitEETestListener {

  private long timestamp;
  private List testInfo = new ArrayList();
  private Map suiteInfo = new HashMap();
  private TestInfo currentInfo;
  private boolean failure = false;
  private boolean singleTest = false;


  public void start(boolean singleTest) {
    setSingleTest(singleTest);
  }


  public abstract void finish();


  public void addError(Test test, Throwable t) {
    getCurrentInfo().addError(t);
    setFailure(true);
  }


  public void addFailure(Test test, Throwable t) {
    getCurrentInfo().addFailure(t);
    setFailure(true);
  }


  public void addFailure(Test test, AssertionFailedError t) {
    getCurrentInfo().addFailure(t);
    setFailure(true);
  }


  public void endTest(Test test) {
    long elapsedTime = System.currentTimeMillis() - getTimestamp();

    getCurrentInfo().setElapsedTime(elapsedTime);
    getTestInfo().add(getCurrentInfo());
    addToSuite(getCurrentInfo());
    setCurrentInfo(null);
  }


  public void startTest(Test test) {
    setCurrentInfo(new TestInfo(test));
    setTimestamp(System.currentTimeMillis());
  }


  // TODO: implement this
  public void runFailed(String message) {
  }


  protected long getTimestamp() {
    return timestamp;
  }


  protected void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }


  protected List getTestInfo() {
    return testInfo;
  }


  protected void setTestInfo(List testInfo) {
    this.testInfo = testInfo;
  }


  protected Map getSuiteInfo() {
    return suiteInfo;
  }


  protected void setSuiteInfo(Map suiteInfo) {
    this.suiteInfo = suiteInfo;
  }


  protected TestInfo getCurrentInfo() {
    return currentInfo;
  }


  protected void setCurrentInfo(TestInfo currentInfo) {
    this.currentInfo = currentInfo;
  }


  protected boolean isFailure() {
    return failure;
  }


  protected void setFailure(boolean failure) {
    this.failure = failure;
  }


  protected boolean isSingleTest() {
    return singleTest;
  }


  protected void setSingleTest(boolean singleTest) {
    this.singleTest = singleTest;
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
