/**
 * $Id: JUnitEEXMLServlet.java,v 1.1 2002-10-01 22:46:19 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/testrunner/org/junitee/servlet/JUnitEEXMLServlet.java,v $
 */

package org.junitee.servlet;


/**
 * This servlet implements the JUnitEE test runner. By default the classloader of this servlet is used also for
 * loading the test classes. This will work in almost any case, but if necessary you can change this behaviour by
 * subclassing this class and overwrite the method {@link #getDynamicClassLoader} to answer the classloader of your
 * choice. By default an xml test report is generated.
 *
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @since   1.5
 */
public class JUnitEEXMLServlet extends JUnitEEServlet {


  /**
   * Answer the default output format of the test report. This implementation returns xml as the default output. It
   * is possible to set the output format by using the <code>output</code> request parameter. Overwrite this method
   * in your subclass to change the output format without the need for the request parameter.
   *
   * @return OUTPUT_XML
   */
  protected String getDefaultOutput() {
    return OUTPUT_XML;
  }

}
