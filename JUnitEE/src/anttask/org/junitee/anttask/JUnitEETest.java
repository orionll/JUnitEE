/*
 * $Id: JUnitEETest.java,v 1.1 2002-08-31 13:59:11 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.anttask;


/**
 * This is a data type used by the JUnitEE task and represents a call to the JUnitEE servlet to run
 * one specific test suite or all available tests.
 *
 * @author  <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1 $
 */
public class JUnitEETest {


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


  private String name;
  private String resource;
  private String errorProperty;
  private String failureProperty;
  private boolean haltOnError;
  private boolean haltOnFailure;
  private boolean runAll;
}
