/*
 * $Id: FormatterElement.java,v 1.5 2003-07-19 22:07:20 o_rossmueller Exp $
 *
 * 2002 Oliver Rossmueller
 *
 */
package org.junitee.anttask;

import java.io.File;

import org.apache.tools.ant.BuildException;

/**
 * This is the JUnitEE equivalent to the JUnit FormatterElement. Unfortunatelly some methods have package visibility in the original
 * FormatterElement so we cannot reuse it here.
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class FormatterElement {

  private String classname;
  private boolean useFile = true;
  private File outFile;
  private String extension;
  private boolean filterTrace;

  public void setType(String type) {
    if ("xml".equals(type)) {
      setClassname("org.junitee.anttask.XMLResultFormatter");
      setExtension(".xml");
    } else if ("plain".equals(type)) {
      setClassname("org.junitee.anttask.PlainResultFormatter");
      setExtension(".txt");
    } else if ("brief".equals(type)) {
      setClassname("org.junitee.anttask.BriefResultFormatter");
      setExtension(".txt");
    } else {
      throw new BuildException("Unknown formatter type '" + type + "'");
    }
  }

  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }

  public boolean isUseFile() {
    return useFile;
  }

  public void setUseFile(boolean useFile) {
    this.useFile = useFile;
  }

  public File getOutFile() {
    return outFile;
  }

  public void setOutFile(File outFile) {
    this.outFile = outFile;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public JUnitEEResultFormatter createFormatter() throws BuildException {
    if (getClassname() == null) {
      throw new BuildException("you must specify type or classname");
    }

    try {
      Class<?> clazz = Class.forName(getClassname());
      Object instance = clazz.newInstance();

      if (instance instanceof JUnitEEResultFormatter) {
        JUnitEEResultFormatter formatter = (JUnitEEResultFormatter)instance;

        if (isUseFile() && getOutFile() != null) {
          formatter.setOutfile(getOutFile());
          formatter.setExtension(getExtension());
          formatter.setFilterTrace(filterTrace);
        } else {
          formatter.setOut(System.out);
        }
        return formatter;
      } else {
        throw new BuildException(getClassname() + " does not implement JUnitEEResultFormatter");
      }
    } catch (ClassNotFoundException e) {
      throw new BuildException(e);
    } catch (InstantiationException e) {
      throw new BuildException(e);
    } catch (IllegalAccessException e) {
      throw new BuildException(e);
    }
  }

  public void setFilterTrace(boolean filtertrace) {
    filterTrace = filtertrace;
  }
}
