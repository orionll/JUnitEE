/*
 * $Id: JUnitEEWarTask.java,v 1.9 2003-09-26 20:57:25 o_rossmueller Exp $
 */
package org.junitee.anttask;


import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;


/**
 * This ant task builds the .war file which will contains the server-side unit tests.
 *
 * @author  <a href="mailto:pierrecarion@yahoo.com">Pierre CARION</a>
 * @version $Revision: 1.9 $
 */
public class JUnitEEWarTask extends War {

  private static final String URLPATTERN_TOKEN = "@urlPattern@";
  private static final String URLPATTERN_REPLACEMENT = "TestServlet";
  private static final String WEBXML_URLPATTERN = "/" + URLPATTERN_REPLACEMENT + "/*";
  private final static String WEBXML_DOCTYPE_2_2 =
    "web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN\" \"http://java.sun.com/j2ee/dtds/web-app_2_2.dtd\"";
  private final static String WEBXML_DOCTYPE_2_3 =
    "web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\" \"http://java.sun.com/j2ee/dtds/web-app_2_3.dtd\"";
  private final static String WEBXML_DISPLAY_NAME = "JunitServletRunner Application";
  private final static String WEBXML_SERVLET_NAME = "JUnitEETestServlet";
  private final static String WEBXML_SERVLET_CLASS = "org.junitee.servlet.JUnitEEServlet";


  private String testjarname;
  private String servletclass = WEBXML_SERVLET_CLASS;
  private List testCases = new ArrayList();
  private List classes = new ArrayList();
  private List ejbRefs = new ArrayList();
  private List ejbLocalRefs = new ArrayList();
  private List resRefs = new ArrayList();
  private File deploymentDescriptor;


  /**
   * This optional parameter define the name of the .jar file
   * which will contain the test classes.
   * This parameter is just the name of a .jar file (without any directory) (eg.: test.jar)
   * If this parameter is not set, the classes will appear as
   * standalone classes in WEB-INF/classes
   * If this parameter is set, the classes will be bundled into a jar
   * file stored in WEB-INF/lib/<testjarname>
   */
  public void setTestjarname(String name) {
    this.testjarname = name;
  }


  /**
   * Set the name of the servlet used in the generated deployment descriptor.
   *
   * @param servletclass
   */
  public void setServletclass(String servletclass) {
    this.servletclass = servletclass;
  }


  public void setWebxml(File descr) {
    deploymentDescriptor = descr;
    super.setWebxml(descr);
  }


  public void addClasses(ZipFileSet fs) {
    classes.add(fs);
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
   * An EjbRef describes a <ejb-ref>
   * in the generated web.xml file.
   *
   * @return new ejbRef element
   */
  public EjbRef createEjbRef() {
    EjbRef ejbRef = new EjbRef();

    ejbRefs.add(ejbRef);
    return ejbRef;
  }


  /**
   * Create a nested ejbLocalRef element.
   * An EjbLocalRef describes a <ejb-local-ref>
   * in the generated web.xml file.
   *
   * @return new ejbRef element
   */
  public EjbLocalRef createEjbLocalRef() {
    EjbLocalRef ejbRef = new EjbLocalRef();

    ejbLocalRefs.add(ejbRef);
    return ejbRef;
  }


  /**
   * Create a nested resourceRef element.
   * @return
   */
  public ResRef createResourceRef() {
    ResRef ref = new ResRef();

    resRefs.add(ref);
    return ref;
  }


  /**
   * Check that all the required parameters have been properly set.
   * A BuildException is thrown if the task is not properly configured
   */
  private void check() throws BuildException {
    if (this.testjarname != null) {
      if (!this.testjarname.endsWith(".jar")) {
        testjarname = testjarname + ".jar";
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
    File webXmlFile = null;
    File indexHtmlFile = null;
    File jarFile = null;
    File lstTestFile = null;
    ZipFileSet fs;

    // check configuration
    check();

    // do the real work now


    try {
      if (deploymentDescriptor == null) {
        webXmlFile = createWebXml();
        setWebxml(webXmlFile);
      }

      if (this.testjarname != null) {
        // if testjarname attribute is set, the classes are bundled
        // in a jar file which is stored the WEB-INF/lib directory
        jarFile = buildClassesJar();
        fs = new ZipFileSet();
        fs.setDir(new File(jarFile.getParent()));
        fs.setIncludes(jarFile.getName());
        fs.setFullpath("WEB-INF/lib/" + this.testjarname);
        addFileset(fs);
      } else {
        Iterator iterator = classes.iterator();

        while (iterator.hasNext()) {
          ZipFileSet zipFileSet = (ZipFileSet)iterator.next();
          super.addClasses(zipFileSet);
        }
      }

      indexHtmlFile = createIndexHtml();

      fs = new ZipFileSet();
      fs.setDir(new File(indexHtmlFile.getParent()));
      fs.setIncludes(indexHtmlFile.getName());
      fs.setFullpath("index.html");
      addFileset(fs);

      lstTestFile = createTestCaseList();
      fs = new ZipFileSet();
      fs.setDir(new File(lstTestFile.getParent()));
      fs.setIncludes(lstTestFile.getName());
      fs.setFullpath("WEB-INF/testCase.txt");
      addFileset(fs);

      super.execute();
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
    log("Building test.jar ...", Project.MSG_DEBUG);
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


  private File createWebXml() throws BuildException {
    try {
      File webXmlFile = File.createTempFile("web", "xml");
      webXmlFile.createNewFile();
      PrintWriter pw = new PrintWriter(new FileOutputStream(webXmlFile));
      pw.println("<?xml version=\"1.0\"?>");

      if (ejbLocalRefs.isEmpty()) {
        pw.println("<!DOCTYPE " + WEBXML_DOCTYPE_2_2 + ">");
      } else {
        pw.println("<!DOCTYPE " + WEBXML_DOCTYPE_2_3 + ">");
      }
      pw.println("");
      pw.println("<web-app>");
      pw.println("  <display-name>" + WEBXML_DISPLAY_NAME + "</display-name>");
      pw.println("");
      pw.println("  <servlet>");
      pw.println("    <servlet-name>" + WEBXML_SERVLET_NAME + "</servlet-name>");
      pw.println("    <description>JUnitEE test harness</description>");
      pw.println("    <servlet-class>" + servletclass + "</servlet-class>");
      pw.println("  </servlet>");
      pw.println("");
      pw.println("  <servlet-mapping>");
      pw.println("    <servlet-name>" + WEBXML_SERVLET_NAME + "</servlet-name>");
      pw.println("    <url-pattern>" + WEBXML_URLPATTERN + "</url-pattern>");
      pw.println("  </servlet-mapping>");
      pw.println("");
      for (Iterator i = resRefs.iterator(); i.hasNext();) {
        ResRef ref = (ResRef)i.next();
        pw.println("");
        pw.println("  <resource-ref>");
        pw.println("    <res-ref-name>" + ref.getResRefName() + "</res-ref-name>");
        pw.println("    <res-type>" + ref.getResType() + "</res-type>");
        pw.println("    <res-auth>" + ref.getResAuth() + "</res-auth>");
        pw.println("  </resource-ref>");
      }

      for (Iterator i = ejbRefs.iterator(); i.hasNext();) {
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
      for (Iterator i = ejbLocalRefs.iterator(); i.hasNext();) {
        EjbLocalRef ejbRef = (EjbLocalRef)i.next();
        pw.println("");
        pw.println("  <ejb-local-ref>");
        pw.println("    <ejb-ref-name>" + ejbRef.getEjbRefName() + "</ejb-ref-name>");
        pw.println("    <ejb-ref-type>" + ejbRef.getEjbRefType() + "</ejb-ref-type>");
        pw.println("    <local-home>" + ejbRef.getLocalHome() + "</local-home>");
        pw.println("    <local>" + ejbRef.getLocal() + "</local>");
        if (ejbRef.getEjbLink() != null) {
          pw.println("    <ejb-link>" + ejbRef.getEjbLink() + "</ejb-link>");
        }
        pw.println("  </ejb-local-ref>");
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

      int index;

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("<!-- ### -->")) {
          pw.print(bufferList.toString());
        } else if ((index = line.indexOf(URLPATTERN_TOKEN)) != -1) {
          pw.print(line.substring(0, index));
          pw.print(URLPATTERN_REPLACEMENT);
          pw.println(line.substring(index + URLPATTERN_TOKEN.length()));
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


  public abstract class AbstractEjbRef {

    private String ejbRefName;
    private String ejbRefType;
    private String ejbLink;


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
    }
  }


  public class EjbRef extends AbstractEjbRef {

    private String home;
    private String remote;


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


    public void check() throws BuildException {
      super.check();
      if (this.home == null) {
        throw new BuildException("You must specify the home attribute", location);
      }
      if (this.remote == null) {
        throw new BuildException("You must specify the remote attribute", location);
      }
    }
  }


  public class EjbLocalRef extends AbstractEjbRef {

    private String localHome;
    private String local;


    public String getLocalHome() {
      return localHome;
    }


    public void setLocalHome(String localHome) {
      this.localHome = localHome;
    }


    public String getLocal() {
      return local;
    }


    public void setLocal(String local) {
      this.local = local;
    }


    public void check() throws BuildException {
      super.check();
      if (this.localHome == null) {
        throw new BuildException("You must specify the localhome attribute", location);
      }
      if (this.local == null) {
        throw new BuildException("You must specify the local attribute", location);
      }
    }
  }

  public class ResRef {

    private String resRefName;
    private String resType;
    private String resAuth;


    public String getResRefName() {
      return resRefName;
    }


    public void setResRefName(String resRefName) {
      this.resRefName = resRefName;
    }


    public String getResType() {
      return resType;
    }


    public void setResType(String resType) {
      this.resType = resType;
    }


    public String getResAuth() {
      return resAuth;
    }


    public void setResAuth(String resAuth) {
      this.resAuth = resAuth;
    }
  }
}
