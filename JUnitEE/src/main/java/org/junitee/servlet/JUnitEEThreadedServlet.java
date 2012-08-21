/*
 * $Id: JUnitEEThreadedServlet.java,v 1.1 2002-11-03 21:25:54 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.servlet;

/**
 * This servlet changes the behaviour of {@link JUnitEEServlet} in the way that by default a thread is forked if more than
 * one test suite is to be exectued.
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class JUnitEEThreadedServlet extends JUnitEEServlet {

  @Override
  protected boolean getDefaultThreadMode() {
    return true;
  }
}
