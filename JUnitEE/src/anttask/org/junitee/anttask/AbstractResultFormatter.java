/*
 * $Id: AbstractResultFormatter.java,v 1.3 2003-07-19 22:02:20 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;


import java.io.*;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;


/**
 * @version $Revision: 1.3 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public abstract class AbstractResultFormatter implements JUnitEEResultFormatter {


  private OutputStream out;
  private File outfile;
  private boolean filterTrace;
  private String extension;
  private boolean batch = false;


  public boolean isBatch() {
    return batch;
  }


  public void setBatch(boolean batch) {
    this.batch = batch;
  }


  public boolean isFilterTrace() {
    return filterTrace;
  }


  public void setFilterTrace(boolean filterTrace) {
    this.filterTrace = filterTrace;
  }


  public void setOut(OutputStream out) {
    this.out = out;
  }


  public void setOutfile(File file) {
    outfile = file;
  }


  public void setExtension(String extension) {
    this.extension = extension;
  }


  public OutputStream getOutput(String testName) throws FileNotFoundException {
    if (out != null) {
      return out;
    }
    String fileName;

    if (isBatch()) {
      fileName = outfile.getAbsolutePath() + testName + extension;
    } else {
      fileName = outfile.getAbsolutePath() + extension;
    }
    return new FileOutputStream(fileName);
  }


  public void flush() throws IOException {
    if (out != null && out != System.out && out != System.err) {
      out.flush();
      try {
        out.close();
      } catch (IOException e) { /* ignore */
      }
    }
  }


  protected String getTestName(Node testSuiteNode) {
    NamedNodeMap attributes = testSuiteNode.getAttributes();
    String name = attributes.getNamedItem("name").getNodeValue();
    String pkg = attributes.getNamedItem("package").getNodeValue();

    if (pkg != null && pkg.length() != 0) {
      return pkg + "." + name;
    } else {
      return name;
    }
  }


}
