/**
 * $Id: TestSuiteInfo.java,v 1.3 2002-09-03 21:07:16 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/testrunner/org/junitee/runner/TestSuiteInfo.java,v $
 */

package org.junitee.runner;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

import junit.framework.*;
import junit.runner.BaseTestRunner;

import org.junitee.runner.JUnitEEOutputProducer;


/**
 * This class holds information about on test.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.3 $
 */
public class TestSuiteInfo {
  private String testClassName;
  private List tests = new ArrayList();
  private List errors = new ArrayList();
  private List failures = new ArrayList();
  private long elapsedTime = 0L;


  public TestSuiteInfo(String className) {
    testClassName = className;
  }


  public void add(TestInfo info) {
    tests.add(info);
    if (info.hasError()) {
      errors.add(info);
    } else if (info.hasFailure()) {
      failures.add(info);
    }
    elapsedTime = elapsedTime + info.getElapsedTime();
  }


  public Collection getTests() {
    return Collections.unmodifiableCollection(tests);
  }


  public boolean hasFailure() {
    return !failures.isEmpty();
  }


  public boolean hasError() {
    return !errors.isEmpty();
  }


  public boolean successful() {
    return !(hasError() || hasFailure());
  }


  public String getTestClassName() {
    return testClassName;
  }


  public long getElapsedTime() {
    return elapsedTime;
  }


  public List getFailures() {
    return failures;
  }

  public List getErrors() {
    return errors;
  }

}

