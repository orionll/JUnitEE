/*
 * $Id: JUnitEEResultFormatter.java,v 1.2 2002-11-17 13:11:53 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;


import java.io.*;

import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
 * This interface is the equivalent to <code>JUnitResultFormatter</code> provided by the JUnit task.
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public interface JUnitEEResultFormatter {

  /**
   * Format the JUnitEE result
   *
   * @param testSuiteNode
   */
  public void format(Node testSuiteNode) throws IOException;

  
  public void setOut(OutputStream out);


  public void setOutfile(File file);


  public void setExtension(String extension);


  public void setFilterTrace(boolean filter);


  void flush() throws IOException;
}
