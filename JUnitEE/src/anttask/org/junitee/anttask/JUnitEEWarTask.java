/*
 * $Id: JUnitEEWarTask.java,v 1.4 2002-09-19 22:03:09 o_rossmueller Exp $
 */
package org.junitee.anttask;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;


/**
 * This ant task builds the .war file which will contains the server-side unit tests.
 *
 * @author  <a href="mailto:pierrecarion@yahoo.com">Pierre CARION</a>
 * @version $Revision: 1.4 $
 */
public class JUnitEEWarTask extends Task {

  /**
   * Set the name of the .war file to create
   * @param file .war file to create
   */
  public void setDestFile(File file) {
    this.destFile = file;
  }


  /**
   * This optional parameter define the name of the .jar file
   * which wile contains the test classes.
   * This parameter is just the name of a .jar file (without any directory) (eg.: test.war)
   * If this parameter is not set, the classes will appear as
   * standalone classes in WEB-INF/classes
   * If this parameter is set, the classes will be bundled into a jar
   * file stored in WEB-INF/lib/<testJarName>
   */
  public void setTestJarName(String name) {
    this.testJarName = name;
  }


  /**
   * The nested lib element specifies a FileSet.
   * All files included in this fileset will end up in the WEB-INF/ lib directory
   * of the war file.
   * @param fs nested lib element
   */
  public void addLib(ZipFileSet fs) {
    this.libs.add(fs);
  }


  /**
   * The nested classes element specifies a FileSet.
   * All files included in this fileset will end up in the WEB- INF/classes directory
   * of the war file.
   * @param fs nested classes element
   */
  public void addClasses(ZipFileSet fs) {
    this.classes.add(fs);
  }


  /**
   * The nested testCases element specifies a FileSet.
   * All files included in this fileset will be considered as the test classes
   * to run. Those classes will appear in the generated index.html and the user will be
   * able to run them individually
   * @param fs nested classes element
   */
  public void addTestCases(FileSet fs) {
    this.testCases.add(fs);
  }


  /**
   * Create a nested ejbRef element.
   * An EjbRef describes a <ejb-ref> which will be eventually appear
   * in the generated web.xml file.
   *
   * @return new ejbRef element
   */
  public EjbRef createEjbRef() {
    EjbRef ejbRef = new EjbRef();

    this.ejbRefs.add(ejbRef);
    return ejbRef;
  }


  /**
   * Check that all the required parameters have been properly set.
   * A BuildException is thrown if the task is not properly configured
   */
  private void check() throws BuildException {
    if (this.destFile == null) {
      throw new BuildException("You must specify the destFile attribute", location);
    }

    if (this.testJarName != null) {
      if (!this.testJarName.endsWith(".jar")) {
        throw new BuildException("the testJarName (" + this.testJarName +
          ")attribute must be terminated by .jar", location);
      }
    }

    // check ejbRef configuration
    for (Iterator i = this.ejbRefs.iterator(); i.hasNext();) {
      EjbRef ejbRef = (EjbRef)i.next();
      ejbRef.check();
    }
  }


  /**
   * Entry point when the task is ran from ant
   */
  public void execute() throws BuildException {
    // check configuration
    check();
    // do the real work now
    executeWarTask();
  }


  /**
   * Create the war file.
   */
  private void executeWarTask() {
    File webXmlFile = null;
    File indexHtmlFile = null;
    File jarFile = null;
    File lstTestFile = null;

    try {
      ZipFileSet fs;
      War war = (War)getProject().createTask("war");

      webXmlFile = createWebXml();
      war.setDestFile(this.destFile);
      war.setWebxml(webXmlFile);

      for (Iterator i = this.libs.iterator(); i.hasNext();) {
        fs = (ZipFileSet)i.next();
        war.addLib(fs);
      }

      if (this.testJarName == null) {
        // if testJarName attribute is not set, the classes are stored
        // in the WEB-INF/classes directory
        for (Iterator i = this.classes.iterator(); i.hasNext();) {
          fs = (ZipFileSet)i.next();
          war.addClasses(fs);
        }
      } else {
        // if testJarName attribute is set, the classes are bundled
        // in a jar file which is stored the WEB-INF/lib directory
        jarFile = buildClassesJar();
        fs = new ZipFileSet();
        fs.setDir(new File(jarFile.getParent()));
        fs.setIncludes(jarFile.getName());
        fs.setFullpath("WEB-INF/lib/" + this.testJarName);
        war.addFileset(fs);
      }

      indexHtmlFile = createIndexHtml();

      fs = new ZipFileSet();
      fs.setDir(new File(indexHtmlFile.getParent()));
      fs.setIncludes(indexHtmlFile.getName());
      fs.setFullpath("index.html");
      war.addFileset(fs);

      lstTestFile = createTestCaseList();
      fs = new ZipFileSet();
      fs.setDir(new File(lstTestFile.getParent()));
      fs.setIncludes(lstTestFile.getName());
      fs.setFullpath("WEB-INF/testCase.txt");
      war.addFileset(fs);

      war.execute();
    } finally {
      if (webXmlFile != null) {
        webXmlFile.delete();
      }

      if (indexHtmlFile != null) {
        indexHtmlFile.delete();
      }

      if (jarFile != null) {
        jarFile.delete();
      }

      if (lstTestFile != null) {
        lstTestFile.delete();
      }
    }
  }


  /**
   * Create jar file containing the classes
   */
  private File buildClassesJar() throws BuildException {
    try {
      File jarFile = File.createTempFile("classes", "jar");
      Jar jar = (Jar)getProject().createTask("jar");
      jar.setDestFile(jarFile);
      for (Iterator i = this.classes.iterator(); i.hasNext();) {
        ZipFileSet fs = (ZipFileSet)i.next();
        jar.addFileset(fs);
      }
      jar.execute();
      return (jarFile);
    } catch (java.io.IOException ex) {
      throw new BuildException("Error creating web.xml", ex);
    }
  }


  private final static String WEBXML_DOCTYPE =
    "web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN\" \"http://java.sun.com/j2ee/dtds/web-app_2_2.dtd\"";
  private final static String WEBXML_DISPLAY_NAME = "JunitServletRunner Application";
  private final static String WEBXML_SERVLET_NAME = "JUnitEETestServlet";
  private final static String WEBXML_SERVLET_CLASS = "org.junitee.servlet.JUnitEEServlet";


  private File createWebXml() throws BuildException {
    try {
      File webXmlFile = File.createTempFile("web", "xml");
      webXmlFile.createNewFile();
      PrintWriter pw = new PrintWriter(new FileOutputStream(webXmlFile));
      pw.println("<?xml version=\"1.0\"?>");
      pw.println("<!DOCTYPE " + WEBXML_DOCTYPE + ">");
      pw.println("");
      pw.println("<web-app>");
      pw.println("  <display-name>" + WEBXML_DISPLAY_NAME + "</display-name>");
      pw.println("");
      pw.println("  <servlet>");
      pw.println("    <servlet-name>" + WEBXML_SERVLET_NAME + "</servlet-name>");
      pw.println("    <description>JUnitEE test harness</description>");
      pw.println("    <servlet-class>" + WEBXML_SERVLET_CLASS + "</servlet-class>");
      pw.println("  </servlet>");
      pw.println("");
      pw.println("  <servlet-mapping>");
      pw.println("    <servlet-name>" + WEBXML_SERVLET_NAME + "</servlet-name>");
      pw.println("    <url-pattern>" + urlPattern + "</url-pattern>");
      pw.println("  </servlet-mapping>");
      pw.println("");
      for (Iterator i = this.ejbRefs.iterator(); i.hasNext();) {
        EjbRef ejbRef = (EjbRef)i.next();
        pw.println("");
        pw.println("  <ejb-ref>");
        pw.println("    <ejb-ref-name>" + ejbRef.getEjbRefName() + "</ejb-ref-name>");
        pw.println("    <ejb-ref-type>" + ejbRef.getEjbRefType() + "</ejb-ref-type>");
        pw.println("    <home>" + ejbRef.getHome() + "</home>");
        pw.println("    <remote>" + ejbRef.getRemote() + "</remote>");
        if (ejbRef.getEjbLink() != null) {
          pw.println("    <ejb-link>" + ejbRef.getEjbLink() + "</ejb-link>");
        }
        pw.println("  </ejb-ref>");

      }
      pw.println("</web-app>");
      pw.close();
      return (webXmlFile);
    } catch (java.io.IOException ex) {
      throw new BuildException("Error creating web.xml", ex);
    }
  }


  private File createIndexHtml() throws BuildException {
    try {
      File file = File.createTempFile("index", "html");
      file.createNewFile();
      PrintWriter pw = new PrintWriter(new FileOutputStream(file));
      InputStream in = getClass().getClassLoader().getResourceAsStream("index.html");
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String line;

      StringBuffer bufferList = new StringBuffer();
      for (Iterator i = this.testCases.iterator(); i.hasNext();) {
        FileSet fs = (FileSet)i.next();

        FileScanner scanner = fs.getDirectoryScanner(project);
        String[] files = scanner.getIncludedFiles();
        for (int j = 0; j < files.length; j++) {
          String className = getTestCaseClassName(files[j]);
          bufferList.append("        <tr><td class=\"cell\"><input type=\"checkbox\" name=\"suite\" value=\"");
          bufferList.append(className).append("\">&nbsp;&nbsp;").append(className).append("</td></tr>\n");
        }
      }

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("<!-- ### -->")) {
          pw.print(bufferList.toString());
        } else {
          pw.println(line);
        }
      }
      reader.close();
      pw.close();
      return (file);
    } catch (java.io.IOException ex) {
      throw new BuildException("Error creating index.html", ex);
    }
  }


  private File createTestCaseList() throws BuildException {
    try {
      File file = File.createTempFile("testcase", "txt");
      file.createNewFile();
      PrintWriter pw = new PrintWriter(new FileOutputStream(file));
      pw.println("# JunitServletRunner");

      for (Iterator i = this.testCases.iterator(); i.hasNext();) {
        FileSet fs = (FileSet)i.next();

        FileScanner scanner = fs.getDirectoryScanner(project);
        String[] files = scanner.getIncludedFiles();
        for (int j = 0; j < files.length; j++) {
          String className = getTestCaseClassName(files[j]);
          pw.println(className);
        }
      }
      pw.close();
      return (file);
    } catch (java.io.IOException ex) {
      throw new BuildException("Error creating test case list", ex);
    }
  }


  /**
   * extract the name of a class from the name of the file
   */
  private String getTestCaseClassName(String fileName) throws BuildException {
    String name = fileName.replace(File.separatorChar, '.');
    int lenSuffix;
    if (name.endsWith(".class")) {
      lenSuffix = 6;
    } else if (name.endsWith(".java")) {
      lenSuffix = 5;
    } else {
      throw new BuildException("TestCase (" + fileName + ") must be .class or .java files");
    }
    String className = name.substring(0, name.length() - lenSuffix);
    return (className);
  }


  public class EjbRef {

    String getEjbRefName() {
      return (this.ejbRefName);
    }


    public void setEjbRefName(String ejbRefName) {
      this.ejbRefName = ejbRefName;
    }


    String getEjbRefType() {
      return (this.ejbRefType);
    }


    public void setEjbRefType(String ejbRefType) {
      this.ejbRefType = ejbRefType;
    }


    String getHome() {
      return (this.home);
    }


    public void setHome(String home) {
      this.home = home;
    }


    String getRemote() {
      return (this.remote);
    }


    public void setRemote(String remote) {
      this.remote = remote;
    }


    String getEjbLink() {
      return (this.ejbLink);
    }


    public void setEjbLink(String ejbLink) {
      this.ejbLink = ejbLink;
    }


    public void check() throws BuildException {
      if (this.ejbRefName == null) {
        throw new BuildException("You must specify the ejbRefName attribute", location);
      }
      if (this.ejbRefType == null) {
        throw new BuildException("You must specify the ejbRefType attribute", location);
      }
      if (this.home == null) {
        throw new BuildException("You must specify the home attribute", location);
      }
      if (this.remote == null) {
        throw new BuildException("You must specify the remote attribute", location);
      }
    }


    private String ejbRefName;
    private String ejbRefType;
    private String home;
    private String remote;
    private String ejbLink;

  }

  // Warning: If you change the value of urlPattern, you may need to update createIndexHtml()
  // method to propagate this change in the <form action="xx" >
  private String urlPattern = "/TestServlet/*";
  private File destFile;
  private String testJarName;
  private List libs = new ArrayList();
  private List classes = new ArrayList();
  private List testCases = new ArrayList();
  private List ejbRefs = new ArrayList();
}
