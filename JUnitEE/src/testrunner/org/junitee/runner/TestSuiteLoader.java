/*
 * $Id: TestSuiteLoader.java,v 1.1 2002-08-31 13:59:11 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.runner;



/**
 * This is an implementation of the <code>junit.runner.TestSuiteLoader</code> interface. The classloader
 * used to load test classes is set in the constructor.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1 $
 */
public class TestSuiteLoader implements junit.runner.TestSuiteLoader {

  private ClassLoader loader;


  /**
   * Create a new instance and set the classloader to be used to load test classes.
   *
   * @param loader  classloader to load test classes
   */
  public TestSuiteLoader(ClassLoader loader) {
    this.loader = loader;
  }


  public Class load(String className) throws ClassNotFoundException {
    return loader.loadClass(className);
  }


  public Class reload(Class aClass) throws ClassNotFoundException {
    return aClass;
  }
}
