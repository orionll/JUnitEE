/**
 * $Id: JUnitEETestListener.java,v 1.2 2002-09-02 23:01:41 o_rossmueller Exp $
 */

package org.junitee.runner;


import junit.framework.TestListener;


/**
 * The listener interface for receiving test runner events.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.2 $
 * @since 1.5
 */
public interface JUnitEETestListener extends TestListener {

  /**
   * This method is called before the first test is executed.
   *
   * @param singleTest true, if only a single test is run
   */
  public void start(boolean singleTest);

  /**
   * This method is called after the last test is executed.
   */
  public void finish();

  /**
   * This method is called when executing a test failed because the test class could not be loaded or an
   * instance could not be created.
   */
  public void runFailed(String message);
}
