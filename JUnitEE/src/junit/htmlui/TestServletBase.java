/**
 * $Id: TestServletBase.java,v 1.4 2002-08-15 19:44:39 o_rossmueller Exp $
 * $Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/src/junit/htmlui/TestServletBase.java,v $
 */

package junit.htmlui;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This is an abstract base class.  In order to use it, you must create
 * a derived class *in the WEB-INF/classes directory* of your web application.
 * That class should simply implement the getDynamicClassLoader() method.
 * This is so that we can use the app server's special class loader which
 * both intelligently reloads changed classes and knows where to look for
 * web application class files.
 *
 * A future version of this servlet & runner would start the test in another
 * thread, store results in the session, and use the HTTP Refresh header to
 * cause the client to poll for summary results until the test is complete.
 * Right now the entire test is run within the scope of a single request, so
 * long-running tests could produce a timeout.
 *
 * @author Jeff Schnitzer (jeff@infohazard.org)
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class TestServletBase extends HttpServlet {
  /**
   * The form parameter which defines the name of the suite
   * class to run.  This parameter can appear more than once
   * to run multiple test suites.
   */
  protected static final String PARAM_SUITE = "suite";
  
  /**
   * The form parameter which defines if
   * method list is shown
   */
  protected static final String METHOD_LIST = "list";
  
  /**
   * The form parameter which defines if
   * resources should be checked to run all included test cases
   */
  protected static final String RUN_ALL = "all";
  protected static final String SEARCH = "search";
  
  /**
   * Answer the classloader used to load the test classes. The default implementation
   * answers the classloader of this class, which usally will be the classloader of
   * the web application the servlet is a part of.
   *
   * If this default behaviour does not work for you, overwrite this method and answer
   * the classloader that fits your needs.
   */
  protected ClassLoader getDynamicClassLoader() {
    return getClass().getClassLoader();
  }
  
  
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    
    searchResources = config.getInitParameter("searchResources");
  }
  
  
  /**
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Set up the response
    response.setContentType("text/html");
    response.setHeader("Cache-Control", "no-cache");
    PrintWriter pw = response.getWriter();
    String methodList = request.getParameter(METHOD_LIST);
    String runAll = request.getParameter(RUN_ALL);
    String[] testClassNames = null;
    
    if (runAll != null) {
      testClassNames = searchForTests(request.getParameterValues(SEARCH));
    } else {
      testClassNames = request.getParameterValues(PARAM_SUITE);
    }
    
    if (testClassNames == null) {
      pw.println("<html>");
      pw.println("<head><title> Error </title></head>");
      pw.println("<body>");
      if (runAll != null) {
        pw.println("<p> No test class(es) specified. </p>");
        pw.println("<p>");
        pw.println("  You requested all test cases to be run by setting the \"all\" parameter,");
        pw.println("  but not test case was found searching the resources");
        pw.print("  \"");
        pw.print(searchResources);
        pw.println("\"");
        pw.println("</p>");
      } else {
        pw.println("<p> No test class(es) specified. </p>");
        pw.println("<p>");
        pw.println("  This servlet uses the form parameter \"suite\" to identify");
        pw.println("  which Test to run.  The parameter can appear multiple times");
        pw.println("  to run multiple tests.");
        pw.println("</p>");
      }
      pw.println("</body>");
      pw.println("</html>");
    }
    else {
      TestRunner tester = null;
      
      if (request.getParameter("sendResult") != null) {
        tester = new ResultTransferTestRunner(pw, this.getDynamicClassLoader());
      } else {
        tester = new TestRunner(pw, this.getDynamicClassLoader());
      }
      if (methodList != null) {
        // show method list on output
        tester.start(testClassNames,true);
      }
      else {
        // don't show method list on output
        tester.start(testClassNames);
      }
    }
  }
  
  /**
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Chain to get so that either will work
    this.doGet(request, response);
  }
  
  
  protected String[] searchForTests(String[] param) {
    StringBuffer buffer = new StringBuffer();
    
    for (int i = 0; i < param.length; i++) {
      buffer.append(param[i]).append(",");
    }
    return searchForTests(buffer.toString());
  }
  
  /**
   * Search all resources set via the searchResources init parameter for classes ending with "Tests"
   */
  protected String[] searchForTests(String param) {
    if (searchResources == null && param == null) {
      return null;
    }
    
    StringTokenizer tokenizer;
    
    if (param != null) {
      tokenizer = new StringTokenizer(param, ",");
    } else {
      tokenizer = new StringTokenizer(searchResources, ",");
    }
    
    List tests = new ArrayList();
    
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken().trim();
      
      try {
        InputStream in = getServletContext().getResourceAsStream("WEB-INF/lib/" + token);
        
        if (in != null) {
          JarInputStream jar = new JarInputStream(in);
          
          while (jar.available() != 0) {
            JarEntry entry = jar.getNextJarEntry();
            String name = entry.getName();
            
            if (name.endsWith("Tests.class") || name.endsWith("Test.class")) {
              tests.add(name.substring(0, name.length() - 6).replace('/', '.'));
            }
          }
        }
      } catch (Exception e) {
      }
    }
    String[] answer = new String[tests.size()];
    tests.toArray(answer);
    return answer;
  }
  
  private String searchResources;
}
