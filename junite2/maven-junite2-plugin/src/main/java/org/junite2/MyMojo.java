package org.junite2;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junitee.anttask.FormatterElement;
import org.junitee.anttask.JUnitEEResultFormatter;
import org.junitee.anttask.JUnitEETask;
import org.junitee.anttask.JUnitEETest;
import org.junitee.anttask.SummaryResultFormatter;
import org.junitee.anttask.XMLResultFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Goal which touches a timestamp file.
 *
 * @goal junitee
 * 
 * @phase install
 */
public class MyMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}/surefire-reports"
     * @required
     */
    private File outputDirectory = new File("target/surefire-reports");
    
    /**
     * Location of the file.
     * @parameter expression="${url}"
     * @required
     */
    private String url=null;
    /**
     * Location of the file.
     * @parameter expression="${threaded}"
     * @optional
     */
    private boolean threaded = false;
    private boolean printSummary = true;
    private boolean runAll =  true;
    private String outfile = "TEST-";
    
    private Vector formatters = new Vector();
    
    public void setRunall(boolean value) {
        runAll = value;
      }


      public boolean getRunall() {
        return runAll;
      }


      public void setOutfile(String file) {
        outfile = file;
      }

    /**
     * Set the URL to call the JUnitEE test servlet.
     *
     * @param url URL of the JUnitEE test servlet
     */
    public void setUrl(String url) {
      this.url = url;
    }


    public void setThreaded(boolean threaded) {
      this.threaded = threaded;
    }

    public void execute()
        throws MojoExecutionException
    {
   	
    	JUnitEETest jeetest = new JUnitEETest();
    	
    	jeetest.setRunall(runAll);
    	
        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File touch = new File( f, outfile );
        
        jeetest.setOutfile(touch.getAbsolutePath());
        
        FormatterElement xmlformat = new FormatterElement();
        
        xmlformat.setType("xml");
        
        jeetest.addFormatter(xmlformat);
        
        FormatterElement txtformat = new FormatterElement();
        
        txtformat.setType("plain");
        
        jeetest.addFormatter(txtformat);
        
        try {
        	execute(jeetest);

	} catch (MojoExecutionException me) {
		throw me;
        	
        } catch (Exception e) {
        	e.printStackTrace();
        	Log log = getLog();
        	log.error(e);
        	throw new MojoExecutionException(e.getMessage());
        	
        }
        
        
    }
    protected void execute(JUnitEETest test) throws Exception {
        StringBuffer arguments = new StringBuffer();
        boolean done;
        String sessionCookie;
        URL requestUrl;
        URLConnection con;
        Log log = getLog();


        arguments.append(url).append("?output=xml");

	

        if (threaded) {
          log.debug("Threaded mode");
          arguments.append("&thread=true");
        }

        if (test.getResource() != null) {
          arguments.append("&resource=").append(test.getResource());
        }
        if (test.getRunall()) {
          arguments.append("&all=true");
        } else if (test.getName() != null) {
          arguments.append("&suite=").append(URLEncoder.encode(test.getName()));
        } else {
          throw new Exception("You must specify the test name or runall attribute");
        }
        if (!test.getFiltertrace()) {
          arguments.append("&filterTrace=false");
        }

		log.debug("url is "+arguments.toString());

        InputStream in = null;
        requestUrl = new URL(arguments.toString());
        
        try {
          
          con = requestUrl.openConnection();
          sessionCookie = con.getHeaderField("Set-Cookie");
          log.debug("Session cookie : " + sessionCookie);
          

          if (sessionCookie != null) {
            int index = sessionCookie.indexOf(';');
            if (index != -1) {
              sessionCookie = sessionCookie.substring(0, index);
            }
          }
          in = con.getInputStream();
          done = parseResult(in, test);
	} catch (MojoExecutionException me) {
		throw me;
        } catch (Exception e) {
        	
          throw new Exception("Error accessing url "+requestUrl.toString(),e);
        } finally {
          if (in != null) {
            try {
              in.close();
            } catch (IOException e) {
            }
            ;
          }
        }

        try {
          while (!done) {
            try {
              log.debug("Sleeping ... ");
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              // continue work
            }
            //log("Get xml again using URL " + requestUrl, Project.MSG_DEBUG);
            con = requestUrl.openConnection();
            if (sessionCookie != null) {
              con.setRequestProperty("Cookie", sessionCookie);
            }
            in = con.getInputStream();
            try {
              done = parseResult(in, test);

		} catch (MojoExecutionException me) {
			throw me;
            } catch (Throwable thr) {
            	log.debug(thr);
			throw new MojoExecutionException(thr.getMessage());
            } finally {
            
              try {
                in.close();
              } catch (IOException e) {
              }
              ;
            }
          }
        
        } catch (Exception e) {
          log.error("Failed to execute test: " + e.getMessage());
          throw new Exception(e);
        }
      }


      private boolean parseResult(InputStream in, JUnitEETest test) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Log log = getLog();

        Document document;
        byte[] buffer = readInput(in);

        try {
          document = builder.parse(new ByteArrayInputStream(buffer));
        } catch (SAXException e) {
          log.error("Invalid xml:\n " + new String(buffer));

          throw new Exception("Unable to parse test result (no valid xml).");
        }

        Element root = document.getDocumentElement();
        if (root.getAttributeNode("unfinished") != null) {
          log.debug(String.valueOf(root.getAttributeNode("unfinished")));
          return false;
        }
        root.normalize();

        NodeList testcases = root.getElementsByTagName("testsuite");
        Vector resultFormatters = createFormatters(test);

	  log.debug("Found " + testcases.getLength()+ " testsuites");

	if (testcases.getLength() <= 0) {
		log.debug("No testsuites found "+new String(buffer));
		throw new MojoExecutionException("No testsuites found!");
}

	  int failure_count = 0;
	  int error_count = 0;


        for (int i = 0; i < testcases.getLength(); i++) {
          Node node = testcases.item(i);
          NamedNodeMap attributes = node.getAttributes();
          String testClass = attributes.getNamedItem("name").getNodeValue();
          String testPkg = attributes.getNamedItem("package").getNodeValue();
          int errors = Integer.parseInt(attributes.getNamedItem("errors").getNodeValue());
          int failures = Integer.parseInt(attributes.getNamedItem("failures").getNodeValue());
          String testName;

          if (testPkg != null && testPkg.length() != 0) {
            testName = testPkg + "." + testClass;
          } else {
            testName = testClass;
          }
          Enumeration enumeration = resultFormatters.elements();

          while (enumeration.hasMoreElements()) {
            JUnitEEResultFormatter formatter = (JUnitEEResultFormatter) enumeration.nextElement();
            log.debug("Calling formatter " + formatter + " for node " + node);
            formatter.format(node);
            formatter.flush();
          }

          if (errors != 0) {
		error_count +=errors;

            
          }
          if (failures != 0) {
            failure_count += failures;
          }

        }

        NodeList errorMessages = root.getElementsByTagName("errorMessage");

        for (int i = 0; i < errorMessages.getLength(); i++) {
          Node message = errorMessages.item(i);
          log.debug(message.getFirstChild().getNodeValue());
        }
        if (errorMessages.getLength() != 0) {
          throw new MojoExecutionException("Test execution failed.");
        }

	  if (error_count > 0 )
		throw new MojoExecutionException(error_count+" errors occured");

	if (failure_count > 0 )
		throw new MojoExecutionException(failure_count +" failures occured");


        return true;
      }


      private byte[] readInput(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int r;
        byte[] buffer = new byte[2048];

        while ((r = in.read(buffer)) != -1) {
          out.write(buffer, 0, r);
        }
        return out.toByteArray();
      }


      private Vector createFormatters(JUnitEETest test) throws Exception {
    	  Log log = getLog();
        Vector answer = new Vector();
        Enumeration enumeration = formatters.elements();

        while (enumeration.hasMoreElements()) {
          FormatterElement element = (FormatterElement) enumeration.nextElement();
          element.setOutFile(test.getOutfile());
          element.setFilterTrace(test.getFiltertrace());
          answer.add(element.createFormatter());
        }

        enumeration = test.getFormatters();
        while (enumeration.hasMoreElements()) {
          FormatterElement element = (FormatterElement) enumeration.nextElement();
          log.debug("outfile=" + test.getOutfile());
          element.setOutFile(test.getOutfile());
          element.setFilterTrace(test.getFiltertrace());
          answer.add(element.createFormatter());
        }
        if (printSummary) {
          log.debug("Adding summary formatter");
          SummaryResultFormatter summary = new SummaryResultFormatter();
          summary.setOut(System.out);
          answer.add(summary);
        }
        log.debug("Formatters: " + answer);
        return answer;
      }
}
