/**
 * $Id: JUnitEETestListener.java,v 1.1 2002-08-31 13:59:11 o_rossmueller Exp $
 */

package org.junitee.runner;


import junit.framework.TestListener;


/**
 * The listener interface for receiving test runner events.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1 $
 * @since 1.5
 */
public interface JUnitEETestListener extends TestListener {


  public void runFailed(String message);
}
