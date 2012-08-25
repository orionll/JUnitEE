package org.junitee.servlet;

/**
 * This servlet changes the behaviour of {@link JUnitEEServlet} in the way that by default a thread is forked if more than
 * one test suite is to be exectued.
 */
public class JUnitEEThreadedServlet extends JUnitEEServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected boolean getDefaultThreadMode() {
    return true;
  }
}
