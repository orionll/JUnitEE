/*
 * $Id: JUnitEETest.java,v 1.3 2002-11-03 17:54:05 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.anttask;

import java.io.*;
import java.util.*;


/**
 * This is a data type used by the JUnitEE task and represents a call to the JUnitEE servlet to run
 * one specific test suite or all available tests.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.3 $
 */
public class JUnitEETest {

  private String name;
  private String resource;
  private String errorProperty;
  private String failureProperty;
  private boolean haltOnError;
  private boolean haltOnFailure;
  private boolean runAll;
  private File outfile;
  private File toDir;
  private Vector formatters = new Vector();
  private boolean filterTrace = true;


  public void addFormatter(FormatterElement formatter) {
    formatters.addElement(formatter);
  }


  public void setName(String value) {
    name = value;
  }


  public String getName() {
    return name;
  }


  public void setResource(String value) {
    resource = value;
  }


  public String getResource() {
    return resource;
  }


  public void setHaltonfailure(boolean value) {
    haltOnFailure = value;
  }


  public boolean getHaltonfailure() {
    return haltOnFailure;
  }


  public void setHaltonerror(boolean value) {
    haltOnError = value;
  }


  public boolean getHaltonerror() {
    return haltOnError;
  }


  public void setErrorproperty(String value) {
    errorProperty = value;
  }


  public String getErrorproperty() {
    return errorProperty;
  }


  public void setFailureproperty(String value) {
    failureProperty = value;
  }


  public String getFailureproperty() {
    return failureProperty;
  }


  public void setRunall(boolean value) {
    runAll = value;
  }


  public boolean getRunall() {
    return runAll;
  }


  public void setOutfile(File file) {
    outfile = file;
  }


  public File getOutfile() {
    if (outfile == null) {
      if (toDir == null) {
        outfile = new File(getFileName());
      } else {
        outfile = new File(toDir, getFileName());
      }
    }
    return outfile;
  }


  public File getTodir() {
    return toDir;
  }


  public void setTodir(File toDir) {
    this.toDir = toDir;
  }


  public boolean getFiltertrace() {
    return filterTrace;
  }


  public void setFiltertrace(boolean filterTrace) {
    this.filterTrace = filterTrace;
  }


  public Enumeration getFormatters() {
    return formatters.elements();
  }

  private String getFileName() {
    if (runAll) {
      return "TEST-all";
    } else {
      return "TEST-" + name;
    }
  }
}
