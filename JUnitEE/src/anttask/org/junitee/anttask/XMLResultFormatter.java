/*
 * $Id: XMLResultFormatter.java,v 1.2 2002-11-03 17:54:05 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;

import java.io.*;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.tools.ant.util.DOMElementWriter;


/**
 * @version $Revision: 1.2 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class XMLResultFormatter extends AbstractResultFormatter implements JUnitEEResultFormatter {

  boolean done = false;

  public void format(Element rootNode, Node testNode) throws IOException {

    if (done) {
      return;
    }
    if (getOutput() != null) {
      Writer writer = null;

      try {
        writer = new OutputStreamWriter(getOutput(), "UTF8");

        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        (new DOMElementWriter()).write(rootNode, writer, 0, "  ");
        writer.flush();
      } finally {
        if (writer != null) {
          writer.close();
        }
      }
    }
    done = true;
  }

}
