/*
 * $Id: AbstractOutput.java,v 1.2 2002-09-03 21:07:16 o_rossmueller Exp $
 */
package org.junitee.output;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.junitee.runner.JUnitEEOutputProducer;
import org.junitee.runner.TestInfo;
import org.junitee.runner.TestSuiteInfo;


public abstract class AbstractOutput implements JUnitEEOutputProducer {

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
    getCurrentInfo().setError(t);
    setFailure(true);
  }


  public void addFailure(Test test, Throwable t) {
    getCurrentInfo().setFailure(t);
    setFailure(true);
  }


  public void addFailure(Test test, AssertionFailedError t) {
    getCurrentInfo().setFailure(t);
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


  protected String exceptionToString(Throwable t) {
    CharArrayWriter buffer = new CharArrayWriter();

    t.printStackTrace(new PrintWriter(buffer));
    return buffer.toString();
  }


  /**
   * Checks to see if t is a RemoteException containing
   * an EJBException, and if it is, prints the nested
   * exception inside the EJBException.  This is necessary
   * because the EJBException.printStackTrace() method isn't
   * intelligent enough to print the nexted exception.
   */
  protected String getEJBExceptionDetail(Throwable t) {
    if (t instanceof java.rmi.RemoteException) {
      java.rmi.RemoteException remote = (java.rmi.RemoteException)t;
      if (remote.detail != null && remote.detail instanceof javax.ejb.EJBException) {
        javax.ejb.EJBException ejbe = (javax.ejb.EJBException)remote.detail;
        if (ejbe.getCausedByException() != null) {

          StringWriter sw = new StringWriter();
          PrintWriter spw = new PrintWriter(sw);
          spw.println("Nested exception is: ");
          ejbe.getCausedByException().printStackTrace(spw);

          return sw.toString();
        }
      }
    }
    return "";
  }


}
