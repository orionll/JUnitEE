/*
 * $Id: TestRunner.java,v 1.1 2002-08-31 13:59:11 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.runner;


import junit.runner.BaseTestRunner;
import junit.runner.TestSuiteLoader;
import junit.framework.*;


/**
 * This is the JUnitEE testrunner.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1 $
 */
public class TestRunner extends BaseTestRunner {

  private TestSuiteLoader loader;
  private JUnitEETestListener listener;


  /**
   * Create a new instance and set the classloader to be used to load test classes.
   *
   * @param loader  classloader to load test classes
   * @param listener  test listener to be notfied
   */
  public TestRunner(ClassLoader loader, JUnitEETestListener listener) {
    this.listener = listener;
    this.loader = new org.junitee.runner.TestSuiteLoader(loader);
  }


  /**
   * Run all tests in the given test classes.
   *
   * @param testClassNames names of the test classes
   */
  public void run(String[] testClassNames) {
    TestResult result = new TestResult();
    result.addListener(listener);

    for (int i = 0; i < testClassNames.length; i++) {
      Test test = getTest(testClassNames[i]);
      test.run(result);
    }
  }


  public TestSuiteLoader getLoader() {
    return loader;
  }


  protected void runFailed(String message) {
    listener.runFailed(message);
  }


  /**
   * Create a test for the given suite class. If the class does not implement the static suite() method,
   * a test suite for the suite class is created.
   *
   * @param suiteClassName
   * @return
   */
  public Test getTest(String suiteClassName) {
    Test test = super.getTest(suiteClassName);

    if (test == null) {
      try {
        test = new TestSuite(loadSuiteClass(suiteClassName));
      } catch (ClassNotFoundException e) {
        String clazz = e.getMessage();

        if (clazz == null) {
          clazz = suiteClassName;
        }
        runFailed("Class not found \"" + clazz + "\"");
        return null;
      }
    }
    return test;
  }


  // TestListener methods; we do nothing here as the events are handled by the listener
  public void addError(Test test, Throwable throwable) {
  }


  public void addFailure(Test test, AssertionFailedError assertionFailedError) {
  }


  public void endTest(Test test) {
  }


  public void startTest(Test test) {
  }

}
