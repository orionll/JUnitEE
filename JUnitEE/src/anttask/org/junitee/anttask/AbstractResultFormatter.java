/*
 * $Id: AbstractResultFormatter.java,v 1.1 2002-11-03 10:49:17 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;

import java.io.*;


/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public abstract class AbstractResultFormatter implements JUnitEEResultFormatter {




  private OutputStream out;
  private boolean filterTrace;


  public boolean isFilterTrace() {
    return filterTrace;
  }


  public void setFilterTrace(boolean filterTrace) {
    this.filterTrace = filterTrace;
  }


  public void setOutput(OutputStream stream) {
    out = stream;
  }


  public OutputStream getOutput() {
    return out;
  }


  public void flush() throws IOException {
    if (out != System.out && out != System.err) {
      out.flush();
      try {
        out.close();
      } catch (IOException e) { /* ignore */
      }
    }
  }





}
