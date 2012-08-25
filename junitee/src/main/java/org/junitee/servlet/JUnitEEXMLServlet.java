package org.junitee.servlet;

/**
 * This servlet implements the JUnitEE test runner. By default the classloader of this servlet is used also for
 * loading the test classes. This will work in almost any case, but if necessary you can change this behaviour by
 * subclassing this class and overwrite the method {@link #getDynamicClassLoader} to answer the classloader of your
 * choice. By default an xml test report is generated.
 */
public class JUnitEEXMLServlet extends JUnitEEServlet {
  private static final long serialVersionUID = 1L;

  /**
   * Answer the default output format of the test report. This implementation returns xml as the default output. It
   * is possible to set the output format by using the <code>output</code> request parameter. Overwrite this method
   * in your subclass to change the output format without the need for the request parameter.
   *
   * @return OUTPUT_XML
   */
  @Override
  protected String getDefaultOutput() {
    return OUTPUT_XML;
  }

}
