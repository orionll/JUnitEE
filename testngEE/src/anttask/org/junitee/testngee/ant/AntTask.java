/*
 * $Id: AntTask.java,v 1.1 2005-10-18 23:29:39 o_rossmueller Exp $
 *
 * Copyright 2005 Oliver Rossmueller
 *
 * This file is part of testngEE.
 *
 * testngEE is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * testngEE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with testngEE; if not, mailto:oliver@tuxerra.com or have a look at
 * http://www.gnu.org/licenses/licenses.html#GPL
 */
package org.junitee.testngee.ant;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.testng.TestNGAntTask;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

/**
 * This is a replacement for the ant task provided by TestNG. If replaces the class name of the main class to have
 * the TestNGEE test runner used instead of the one provided by TestNG.
 *
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 */
public class AntTask extends TestNGAntTask {

  private String runnerUrl;
  private Integer m_verbose = null;


  public void setVerbose(Integer verbose) {
    m_verbose = verbose;
  }

  public void setRunnerUrl(String runnerUrl) throws MalformedURLException {
    this.runnerUrl = runnerUrl;
  }

  public void setDumpCommand(boolean b) {
    throw new BuildException("attribute dumpCommand not supported by testngee task");
  }

  public void setClasspath(Path path) {
    throw new BuildException("attribute classpath not supported by testngee task; deploy required libraries together with TestNGEE servlet");
  }

  public void setClasspathRef(Reference reference) {
    throw new BuildException("attribute classpathref not supported by testngee task; deploy required libraries together with TestNGEE servlet");
  }

  public void setSourcedir(Path path) {
    throw new BuildException("attribute sourcedir not supported by testngee task");
  }

  public void setSourceDirRef(Reference reference) {
    throw new BuildException("attribute sourcedirref not supported by testngee task");
  }

  public void setEnableAssert(boolean b) {
    throw new BuildException("attribute enableassert not supported by testngee task");
  }

  public void setTarget(String string) {
    throw new BuildException("attribute target not supported by testngee task");
  }

  public void execute() throws BuildException {
    validateOptions();

    HttpClient client = new HttpClient();

    MultipartPostMethod method = new MultipartPostMethod(runnerUrl);

    if (null != m_isJUnit) {
      if (m_isJUnit.booleanValue()) {
        method.addParameter("isJunit", "true");
      }
    }

//    if (null != m_verbose) {
//      cmd.createArgument().setValue(TestNGCommandLineArgs.LOG);
//      cmd.createArgument().setValue(m_verbose.toString());
//    }

    if ((null != m_outputDir)) {
      if (! m_outputDir.exists()) {
        m_outputDir.mkdirs();
      }
      if (! m_outputDir.isDirectory()) {
        throw new BuildException("Output directory is not a directory: " + m_outputDir);
      }
    }

    if ((null != m_testjar)) {
      method.addParameter("testjar", m_testjar.getName());
    }

    if (null != m_groups) {
      method.addParameter("groups", m_groups);
    }

    if (m_classFilesets.size() > 0) {
      for (String file : fileset(m_classFilesets)) {
        method.addParameter("classfile", file);
      }
    }

    if (m_xmlFilesets.size() > 0) {
      for (String file : fileset(m_xmlFilesets)) {
        try {
          method.addParameter("file", new File(file));
        } catch (FileNotFoundException e) {
          throw new BuildException(e);
        }
      }
    }

    int exitValue = -1;

    try {
      client.executeMethod(method);
      InputStream in = method.getResponseBodyAsStream();
      ZipInputStream zipIn = new ZipInputStream(in);
      ZipEntry zipEntry;

      while ((zipEntry = zipIn.getNextEntry()) != null) {
        byte[] buffer = new byte[4096];
        File file = new File(m_outputDir, zipEntry.getName());
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        int r;

        while ((r = zipIn.read(buffer)) != -1) {
          out.write(buffer, 0, r);
        }
        out.close();
        zipIn.closeEntry();
      }

      zipIn.close();
      exitValue = 0;
    } catch (IOException e) {
      throw new BuildException(e);
    }
    actOnResult(exitValue);
  }

  protected void validateOptions() throws BuildException {
    super.validateOptions();
    if (this.runnerUrl == null) {
      throw new BuildException("runnerUrl is required");
    }
  }

  private List<String> fileset(List<FileSet> filesets) throws BuildException {
    List<String> files = new ArrayList<String>();

    for (Iterator<FileSet> iterator = filesets.iterator(); iterator.hasNext();) {
      FileSet fileset = iterator.next();
      DirectoryScanner ds = fileset.getDirectoryScanner(getProject());

      for (String file : ds.getIncludedFiles()) {
        files.add(ds.getBasedir() + File.separator + file);
      }
    }

    return files;
  }
}
