package org.junitee.anttask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;

/**
 * This ant task builds the .war file which will contains the server-side unit tests.
 *
 * @author  <a href="mailto:pierrecarion@yahoo.com">Pierre CARION</a>
 * @version $Revision: 1.1 $
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
     * The nested lib element specifies a FileSet. 
     * All files included in this fileset will  end up in the WEB-INF/ lib directory of the war file.
     * @param fs nested lib element
     */
    public void addLib(ZipFileSet fs) {
        this.libs.add(fs);
    }
    
    /**
     * The nested classes element specifies a FileSet. 
     * All files included in this fileset will end up in the WEB- INF/classes directory of the war file.
     * @param fs nested classes element
     */
    public void addClasses(ZipFileSet fs) {
        this.classes.add(fs);
    }
    
    public void addTestCases(FileSet fs) {
        this.testCases.add(fs);
    }

   /**
    * Create a nested test element.
    *
    * @return new test element
    */
    public EjbRef createEjbRef() {
      EjbRef ejbRef = new EjbRef();

      this.ejbRefs.add(ejbRef);
      return ejbRef;
    }
    
    public void check() throws BuildException {
        if (this.destFile == null) {
            throw new BuildException("You must specify the destFile attribute", location);
        }
        for(Iterator i = this.ejbRefs.iterator() ; i.hasNext() ; ) {
            EjbRef ejbRef = (EjbRef)i.next();
            ejbRef.check();
        }
    }

    public void execute() throws BuildException {
        check();
        executeWarTask();
    }
    
    private void executeWarTask() {
        File webXmlFile = null;
        File indexHtmlFile = null;
        
        try {
            War war = (War) getProject().createTask("war");
            webXmlFile = createWebXml();
            war.setDestFile(this.destFile);
            war.setWebxml(webXmlFile);
            
            for(Iterator i = this.libs.iterator() ; i.hasNext() ; ) {
                ZipFileSet fs = (ZipFileSet) i.next();
                war.addLib(fs);
            }
            
            for(Iterator i = this.classes.iterator() ; i.hasNext() ; ) {
                ZipFileSet fs = (ZipFileSet) i.next();
                war.addClasses(fs);
            }
            
            ZipFileSet fs;
            indexHtmlFile = createIndexHtml();
            
            fs = new ZipFileSet();
            fs.setDir(new File(indexHtmlFile.getParent()));
            fs.setIncludes(indexHtmlFile.getName());
            fs.setFullpath("/index.html");
            war.addFileset(fs);
            
            war.execute();
        } finally {
            if (webXmlFile != null) {
                webXmlFile.delete();
            }
            
            if (indexHtmlFile != null) {
                indexHtmlFile.delete();
            }
            
        }
    }
    
    

    private final static String WEBXML_DOCTYPE = 
        "web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN\" \"http://java.sun.com/j2ee/dtds/web-app_2_2.dtd\"";
    private final static String WEBXML_DISPLAY_NAME = "JunitServletRunner Application"; 
    private final static String WEBXML_SERVLET_NAME = "JUnitEETestServlet"; 
    private final static String WEBXML_SERVLET_CLASS = "g.junitee.servlet.JUnitEEServlet"; 
        
    private File createWebXml() throws BuildException {
        try {
            File webXmlFile = File.createTempFile("web", "xml");
            webXmlFile.createNewFile();
            PrintWriter pw = new PrintWriter(new FileOutputStream(webXmlFile));
            pw.println("<?xml version=\"1.0\"?>");
            pw.println("<!DOCTYPE "+WEBXML_DOCTYPE+">");
            pw.println("");
            pw.println("<web-app>");
            pw.println("  <display-name>"+WEBXML_DISPLAY_NAME+"</display-name>");
            pw.println("");
            pw.println("  <servlet>");
            pw.println("    <servlet-name>"+WEBXML_SERVLET_NAME+"</servlet-name>");
            pw.println("    <description>JUnitEE test harness</description>");
            pw.println("    <servlet-class>"+WEBXML_SERVLET_CLASS+"</servlet-class>");
            pw.println("  </servlet>");
            pw.println("");
            pw.println("  <servlet-mapping>");
            pw.println("    <servlet-name>"+WEBXML_SERVLET_NAME+"</servlet-name>");
            pw.println("    <url-pattern>"+urlPattern+"</url-pattern>");
            pw.println("  </servlet-mapping>");
            pw.println("");
            for(Iterator i = this.ejbRefs.iterator() ; i.hasNext() ; ) {
                EjbRef ejbRef = (EjbRef)i.next();
                pw.println("");
                pw.println("  <ejb-ref>");
                pw.println("    <ejb-ref-name>"+ejbRef.getEjbRefName()+"</ejb-ref-name>");
                pw.println("    <ejb-ref-type>"+ejbRef.getEjbRefType()+"</ejb-ref-type>");
                pw.println("    <home>"+ejbRef.getHome()+"</home>");
                pw.println("    <remote>"+ejbRef.getRemote()+"</remote>");
                if (ejbRef.getEjbLink() != null) {
                    pw.println("    <ejb-link>"+ejbRef.getEjbLink()+"</ejb-link>");
                }
                pw.println("  </ejb-ref>");
                
            }
            pw.println("</web-app>");
            pw.close();
            return(webXmlFile);
        } catch(java.io.IOException ex) {
            throw new BuildException("Error creating web.xml",ex);
        }
    }
    
    private File createIndexHtml() throws BuildException {
        try {
            File file = File.createTempFile("index", "html");
            file.createNewFile();
            PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            pw.println("<html>");
            pw.println("	<head>");
            pw.println("		<title> JUnitEE Unit Test Framework </title>");
            pw.println("	</head>");
            pw.println("	<body>");
            pw.println("		<p>");
            pw.println("			Welcome to <em> JUnitEE </em>.  You have");
            pw.println(" 			two ways to choose which test suite(s) to run:");
            pw.println("		</p>");
            pw.println("		<hr>");
            pw.println("		<p>");
            pw.println("			You may type in the name of a test suite:");
            pw.println(" 			<br>");
            pw.println("			<form action=\"TestServlet\" method=\"get\" name=\"youTypeItForm\">");
            pw.println("				<input type=\"text\" name=\"suite\" size=60>");
            pw.println("				<input type=\"submit\" value=\"Run\">");
            pw.println("			</form>");
            pw.println("		</p>");
            pw.println("		<hr>");
            pw.println("		<p>");
            pw.println("			You may pick one or more of the following test suites:");
            pw.println(" 			<br>");
            pw.println("			<form action=\"TestServlet\" method=\"get\" name=\"youPickItForm\">");
            pw.println("				<select name=\"suite\" multiple>");
            for(Iterator i = this.testCases.iterator() ; i.hasNext() ; ) {
                FileSet fs = (FileSet) i.next();

                FileScanner scanner = fs.getDirectoryScanner(project);
                String[] files = scanner.getIncludedFiles();
                for (int j = 0; j < files.length; j++) {
                    String name = files[j];
                    if (! name.endsWith(".class")) {
                        new BuildException("TestCase must be .class:"+name);
                    }
                    String name2 = name.replace(File.separatorChar,'.');
                    String className = name2.substring(0,name2.length() - 6);
                    pw.println("					<option value=\""+className+"\">");
                    pw.println("						"+className);
                    pw.println("					</option>");
                }
            }
            pw.println("				</select>");
            pw.println("				<input type=\"submit\" value=\"Run\">");
            pw.println("			</form>");
            pw.println("		</p>");
            pw.println("	</body>");
            pw.println("</html>");
            pw.close();
            return(file);
        } catch(java.io.IOException ex) {
            throw new BuildException("Error creating index.html",ex);
        }
    }
    


    public class EjbRef {
        String getEjbRefName() {
            return(this.ejbRefName);
        }
        
        public void setEjbRefName(String ejbRefName) {
            this.ejbRefName = ejbRefName;
        }
        
        String getEjbRefType() {
            return(this.ejbRefType);
        }
        
        public void setEjbRefType(String ejbRefType) {
            this.ejbRefType = ejbRefType;
        }
        
        String getHome() {
            return(this.home);
        }
        
        public void setHome(String home) {
            this.home = home;
        }
        
        String getRemote() {
            return(this.remote);
        }
        
        public void setRemote(String remote) {
            this.remote = remote;
        }
        
        String getEjbLink() {
            return(this.ejbLink);
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
    private List libs = new ArrayList ();
    private List classes = new ArrayList ();
    private List testCases = new ArrayList ();
    private List ejbRefs = new ArrayList();
}
