/*
 * $Id: RequiresDecoration.java,v 1.1 2003-05-29 10:59:33 o_rossmueller Exp $
 *
 * (c) 2003 Oliver Rossmueller
 */
package org.junitee.runner;


import junit.framework.Test;


/**
 * Interface to tell a test runner that a test requires decoration.
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public interface RequiresDecoration {

  /**
   * Decorate this test. This method will be called only if a single test is
   * executed.
   *
   * @return
   */
  public Test decorate();
}
