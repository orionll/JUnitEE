/*
 * $Id: TestRunner.java,v 1.3 2002-09-02 23:01:41 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.runner;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import junit.runner.BaseTestRunner;
import junit.runner.TestSuiteLoader;
import junit.framework.*;


/**
 * This is the JUnitEE testrunner.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.3 $
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
    listener.start(false);

    for (int i = 0; i < testClassNames.length; i++) {
      Test test = getTest(testClassNames[i]);
      test.run(result);
    }

    listener.finish();
  }


  public void run(String testClassName, String testName) {
    TestResult result = new TestResult();
    result.addListener(listener);

    listener.start(true);

    Test test = getTest(testClassName, testName);
    test.run(result);

    listener.finish();
  }

  public TestSuiteLoader getLoader() {
    return loader;
  }


  protected void runFailed(String message) {
    listener.runFailed(message);
  }


  /**
   * Create a test suite for the given suite class. If the class does not implement the static suite() method,
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


  protected Test getTest(String suiteClassName, String testName) {
    try {
      Class clazz = loadSuiteClass(suiteClassName);
      Constructor constructor = clazz.getConstructor(new Class[]{String.class});
      return (Test)constructor.newInstance(new Object[]{testName});
    } catch (ClassNotFoundException e) {
      runFailed("Class not found \"" + suiteClassName + "\"");
      return null;
    } catch (InstantiationException e) {
      runFailed("Could not create test \"" + suiteClassName + "." + testName + "\"");
    } catch (IllegalAccessException e) {
      runFailed("Could not create test \"" + suiteClassName + "." + testName + "\"");
    } catch (InvocationTargetException e) {
      runFailed("Could not create test \"" + suiteClassName + "." + testName + "\"");
    } catch (NoSuchMethodException e) {
      runFailed("Could not create test \"" + suiteClassName + "." + testName + "\"");
    }
    return null;
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
