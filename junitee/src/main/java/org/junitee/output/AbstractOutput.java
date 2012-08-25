package org.junitee.output;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.List;

import org.junitee.runner.TestInfo;
import org.junitee.runner.TestRunnerResults;
import org.junitee.runner.TestSuiteInfo;

public abstract class AbstractOutput implements OutputProducer {

  private TestRunnerResults results;
  private boolean filterTrace = true;

  public AbstractOutput(TestRunnerResults results, boolean filterTrace) {
    this.results = results;
    this.filterTrace = filterTrace;
  }

  @Override
  public abstract void render();

  public synchronized TestInfo getCurrentInfo() {
    synchronized (results) {
      return results.getCurrentInfo();
    }
  }

  protected long getTimestamp() {
    synchronized (results) {
      return results.getTimestamp();
    }
  }

  protected List<TestSuiteInfo> getSuiteInfo() {
    synchronized (results) {
      return results.getSuiteInfo();
    }
  }

  protected boolean isFailure() {
    synchronized (results) {
      return results.isFailure();
    }
  }

  protected boolean isFilterTrace() {
    return filterTrace;
  }

  protected boolean isSingleTest() {
    synchronized (results) {
      return results.isSingleTest();
    }
  }

  protected boolean isFinished() {
    synchronized (results) {
      return results.isFinished();
    }
  }

  protected boolean isStopped() {
    synchronized (results) {
      return results.isStopped();
    }
  }

  protected List<String> getErrorMessages() {
    synchronized (results) {
      return results.getErrorMessages();
    }
  }

  protected String exceptionToString(Throwable t) {
    CharArrayWriter buffer = new CharArrayWriter();

    t.printStackTrace(new PrintWriter(buffer));
    return buffer.toString();
  }
}
