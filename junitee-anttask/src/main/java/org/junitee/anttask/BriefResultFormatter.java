/*
 * $Id: BriefResultFormatter.java,v 1.2 2002-11-17 13:11:53 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;

import java.io.IOException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @version $Revision: 1.2 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class BriefResultFormatter extends PlainResultFormatter {

  @Override
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
