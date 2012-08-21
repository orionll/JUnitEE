/*
 * $Id: AbstractOutput.java,v 1.7 2002-11-27 23:53:21 o_rossmueller Exp $
 */
package org.junitee.output;


import java.io.*;
import java.util.*;

import org.junitee.runner.TestRunnerResults;
import org.junitee.runner.TestInfo;


/**
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.7 $
 * @since 1.5
 */
public abstract class AbstractOutput implements OutputProducer {

  private TestRunnerResults results;
  private boolean filterTrace = true;


  public AbstractOutput(TestRunnerResults results, boolean filterTrace) {
    this.results = results;
    this.filterTrace = filterTrace;
  }


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


  protected List getSuiteInfo() {
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


  protected List getErrorMessages() {
    synchronized (results) {
      return results.getErrorMessages();
    }
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
