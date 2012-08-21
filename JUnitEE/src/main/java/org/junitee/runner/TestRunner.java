/*
 * $Id: TestRunner.java,v 1.13 2006-04-09 14:14:09 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;

/**
 * This is the JUnitEE testrunner.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.13 $
 * @since   1.5
 */
public class TestRunner extends BaseTestRunner {

  private TestRunnerListener listener;
  private volatile boolean run = false;
  private boolean forkThread;

  /**
   * Create a new instance and set the classloader to be used to load test classes.
   *
   * @param listener  test listener to be notfied
   */
  public TestRunner(TestRunnerListener listener, boolean forkThread) {
    this.listener = listener;
    this.forkThread = forkThread;
  }

  public void stop() {
    run = false;
    // notify the listener immediatley so we can display this information
    listener.setStopped();
  }

  /**
   * Run all tests in the given test classes.
   *
   * @param testClassNames names of the test classes
   */
  public void run(final String[] testClassNames) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        TestResult result = new TestResult();
        result.addListener(listener);
        listener.start(false);

        for (int i = 0; i < testClassNames.length; i++) {
          String testClassName = testClassNames[i];
          try {
            Class<?> clazz = loadSuiteClass(testClassName);

            Test test;
            if (Test.class.isAssignableFrom(clazz)) {
              test = getTest(testClassNames[i]);
            } else {
              test = new JUnit4TestAdapter(clazz);
            }

            if (test != null) {
              test.run(result);
            }
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              // back to work
            }
            if (!run) {
              // if this was not the last test
              if (i != testClassNames.length - 1) {
                runFailed("Execution was stopped");
                break;
              }
            }
          } catch (ClassNotFoundException e) {
            runFailed("Class not found \"" + testClassName + "\"");
          }
        }
        listener.finish();
      }
    };
    run = true;
    if (forkThread) {
      Thread thread = new Thread(runnable, toString());
      thread.start();
    } else {
      runnable.run();
    }
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

  @Override
  protected void runFailed(String className) {
    listener.runFailed(className);
  }

  protected Test getTest(String suiteClassName, final String testName) {
    try {
      Class<?> clazz = loadSuiteClass(suiteClassName);

      if (Test.class.isAssignableFrom(clazz)) {
        Constructor<?> constructor = clazz.getConstructor(new Class[] { String.class });
        Test test = (Test)constructor.newInstance(new Object[] { testName });

        if (test instanceof RequiresDecoration) {
          test = ((RequiresDecoration)test).decorate();
        }
        return test;
      }

      JUnit4TestAdapter adapter = new JUnit4TestAdapter(clazz);

      adapter.filter(new Filter() {
        @Override
        public boolean shouldRun(Description description) {
          return description.getMethodName().equals(testName);
        }

        @Override
        public String describe() {
          return null;
        }

      });

      return adapter;
    } catch (ClassNotFoundException e) {
      runFailed("Class not found \"" + suiteClassName + "\"");
    } catch (InstantiationException e) {
      runFailed("Could not create instance of class \"" + suiteClassName + "\" (" + e.getMessage() + ")");
    } catch (IllegalAccessException e) {
      runFailed("Could not create instance of class \"" + suiteClassName + "\" (" + e.getMessage() + ")");
    } catch (InvocationTargetException e) {
      runFailed("Could not create instance of class \"" + suiteClassName + "\" (" + e.getMessage() + ")");
    } catch (NoSuchMethodException e) {
      runFailed("Missing constructor \"" + suiteClassName + "\"(String) in class \"" + suiteClassName + "\"");
    } catch (NoTestsRemainException e) {
      runFailed("Class \"" + suiteClassName + "\" does not have test \"" + testName + "\"");
    }
    return null;
  }

  // TestListener methods; we do nothing here as the events are handled by the listener
  @Override
  public synchronized void addError(Test test, Throwable throwable) {
  }

  @Override
  public synchronized void addFailure(Test test, AssertionFailedError assertionFailedError) {
  }

  @Override
  public synchronized void endTest(Test test) {
  }

  @Override
  public synchronized void startTest(Test test) {
  }

  @Override
  public void testStarted(String s) {
  }

  @Override
  public void testEnded(String s) {
  }

  @Override
  public void testFailed(int i, Test test, Throwable throwable) {
  }

}
