package org.junitee.runner;

import java.util.Collection;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * This is the JUnitEE testrunner.
 */
public class TestRunner extends RunListener {
  private TestRunnerResults listener;
  private boolean forkThread;
  private JUnitCore junitCore;

  /**
   * Create a new instance and set the classloader to be used to load test
   * classes.
   * 
   * @param loader
   *            classloader to load test classes
   * @param listener
   *            test listener to be notfied
   */
  public TestRunner(TestRunnerResults listener, boolean forkThread) {
    this.listener = listener;
    this.forkThread = forkThread;
    junitCore = new JUnitCore();
    junitCore.addListener(this);
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    listener.addFailure(failure.getDescription(), failure);
    super.testFailure(failure);
  }

  @Override
  public void testFinished(Description description) throws Exception {

    listener.endTest(description);
    super.testFinished(description);
  }

  @Override
  public void testIgnored(Description description) throws Exception {
    super.testIgnored(description);
  }

  @Override
  public void testRunFinished(Result result) throws Exception {
    listener.finish();
    super.testRunFinished(result);
  }

  @Override
  public void testRunStarted(Description description) throws Exception {

    super.testRunStarted(description);
    listener.start(false);
    if (description.testCount() == 1) {
      listener.start(true);
    }
  }

  @Override
  public void testStarted(Description description) throws Exception {
    super.testStarted(description);
    listener.startTest(description);
  }

  public void stop() {
    // notify the listener immediatley so we can display this information
    listener.setStopped();
  }

  /**
   * Run all tests in the given test classes.
   * 
   * @param testClassNames
   *            names of the test classes
   */
  public void run(final Collection<String> testClassNames) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {

        listener.start(false);

        for (String testClassName : testClassNames) {

          Class<?> clazz = null;
          try {
            clazz = getClass().getClassLoader().loadClass(testClassName);
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }

          if (clazz != null) {
            Request req = Request.aClass(clazz);
            junitCore.run(req);
          }
        }
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          // back to work
        }

        listener.finish();
      }
    };

    if (forkThread) {
      Thread thread = new Thread(runnable, toString());
      thread.start();
    } else {
      runnable.run();
    }
  }

  public void run(final String testClassName, final String testName) {
    final Filter filter = new Filter() {
      @Override
      public String describe() {
        return "Only Run " + testClassName + " test " + testName;
      }

      @Override
      public boolean shouldRun(Description description) {
        if (description.getMethodName().equals(testName)) {
          return true;
        }
        return false;
      }
    };

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        Request runRequest = null;

        Class<?> clazz = null;;
        try {
          clazz = this.getClass().getClassLoader().loadClass(testClassName);
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }

        if (clazz != null) {
          Request req = Request.aClass(clazz);

          runRequest = req.filterWith(filter);

        }
        listener.start(false);

        if (runRequest != null) {
          junitCore.run(runRequest);
        }
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          // back to work
        }

        listener.finish();
      }
    };

    if (forkThread) {
      Thread thread = new Thread(runnable, toString());
      thread.start();
    } else {
      runnable.run();
    }
  }
}
