/*
 * $Id: TestRunner.java,v 1.6 2002-10-01 21:06:43 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.runner;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.runner.BaseTestRunner;
import junit.runner.TestSuiteLoader;
import junit.framework.*;


/**
 * This is the JUnitEE testrunner.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.6 $
 * @since   1.5
 */
public class TestRunner extends BaseTestRunner {

  private TestSuiteLoader loader;
  private JUnitEEOutputProducer listener;


  /**
   * Create a new instance and set the classloader to be used to load test classes.
   *
   * @param loader  classloader to load test classes
   * @param listener  test listener to be notfied
   */
  public TestRunner(ClassLoader loader, JUnitEEOutputProducer listener) {
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
      if (test != null) {
        test.run(result);
      }
    }

    listener.finish();
  }


  public void run(String testClassName, String testName) {
    TestResult result = new TestResult();
    result.addListener(listener);

    listener.start(true);

    Test test = getTest(testClassName, testName);
    if (test != null) {
      test.run(result);
    }

    listener.finish();
  }

  public TestSuiteLoader getLoader() {
    return loader;
  }


  protected void runFailed(String className) {
    listener.runFailed(className);
  }




  protected Test getTest(String suiteClassName, String testName) {
    try {
      Class clazz = loadSuiteClass(suiteClassName);
      Constructor constructor = clazz.getConstructor(new Class[]{String.class});
      Method tmp = clazz.getMethod(testName, new Class[0]);
      return (Test)constructor.newInstance(new Object[]{testName});
    } catch (ClassNotFoundException e) {
      runFailed("Class not found \"" + suiteClassName + "\"");
    } catch (InstantiationException e) {
      runFailed("Could not create instance of class \"" + suiteClassName + "\" (" + e.getMessage() + ")");
    } catch (IllegalAccessException e) {
      runFailed("Could not create instance of class \"" + suiteClassName + "\" (" + e.getMessage() + ")");
    } catch (InvocationTargetException e) {
      runFailed("Could not create instance of class \"" + suiteClassName + "\" (" + e.getMessage() + ")");
    } catch (NoSuchMethodException e) {
      runFailed("No method \"" + testName + "\" in class \"" + suiteClassName  + "\"");
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
