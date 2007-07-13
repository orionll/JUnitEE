/*
 * $Id: BriefResultFormatter.java,v 1.1.1.1 2007-07-13 23:45:13 martinfr62 Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.apache.tools.ant.BuildException;


/**
 * @version $Revision: 1.1.1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class BriefResultFormatter extends PlainResultFormatter {


  public void format(Node testSuiteNode) throws IOException {
    NamedNodeMap attributes = testSuiteNode.getAttributes();
    int errors = Integer.parseInt(attributes.getNamedItem("errors").getNodeValue());
    int failures = Integer.parseInt(attributes.getNamedItem("failures").getNodeValue());

    if (errors + failures == 0) {
      // only add failures to the output
      return;
    }

    super.format(testSuiteNode);
  }
}
