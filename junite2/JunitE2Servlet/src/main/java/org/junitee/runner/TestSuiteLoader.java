/*
 * $Id: TestSuiteLoader.java,v 1.1.1.1 2007-07-13 23:45:17 martinfr62 Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.runner;



/**
 * The classloader
 * used to load test classes is set in the constructor.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1.1.1 $
 * @since   1.5
 */
public class TestSuiteLoader 
 
{

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
