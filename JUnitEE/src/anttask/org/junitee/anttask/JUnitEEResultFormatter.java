/*
 * $Id: JUnitEEResultFormatter.java,v 1.1 2002-11-03 10:49:17 o_rossmueller Exp $
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
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public interface JUnitEEResultFormatter {

  /**
   * Format the JUnitEE result
   *
   * @param testNode
   */
  public void format(Element rootNode, Node testNode) throws IOException;


  public void setOutput(OutputStream stream);


  public void setFilterTrace(boolean filter);


  void flush() throws IOException;
}
