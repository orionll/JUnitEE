package org.junitee.anttask;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * A class that helps with authentication against the HTTP servlet.
 * 
 * @author skaringa
 */
public class AuthImpl extends Authenticator {

  private String _user;
  private String _password;

  public AuthImpl(String user, String password) {
    _user = user;
    _password = password;
  }

  @Override
  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(_user, _password.toCharArray());
  }
}
