/**
 * $Id: TestSuiteInfo.java,v 1.1.1.1 2007-07-13 23:45:17 martinfr62 Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/junite2/JunitE2Servlet/src/main/java/org/junitee/runner/TestSuiteInfo.java,v $
 */

package org.junitee.runner;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

//import junit.framework.*;
//import junit.runner.BaseTestRunner;

import org.junitee.runner.TestRunnerListener;


/**
 * This class holds information about on test.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1.1.1 $
 * @since   1.5
 */
public class TestSuiteInfo {
  private String testClassName;
  private ArrayList tests = new ArrayList();
  private ArrayList errors = new ArrayList();
  private ArrayList failures = new ArrayList();
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


  public synchronized Collection getTests() {
    return (Collection)tests.clone();
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


  public synchronized List getFailures() {
    return (List)failures.clone();
  }

  public synchronized List getErrors() {
    return (List)errors.clone();
  }

}

