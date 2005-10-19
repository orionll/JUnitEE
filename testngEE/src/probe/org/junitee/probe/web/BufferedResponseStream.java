/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
package org.junitee.probe.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BufferedResponseStream extends ServletOutputStream {

  protected ByteArrayOutputStream baos = null;
  protected boolean closed = false;
  protected HttpServletResponse response = null;



  public BufferedResponseStream(HttpServletResponse response) throws IOException {
    super();
    closed = false;
    this.response = response;
    baos = new ByteArrayOutputStream();
  }

  public byte[] getBytes() {
    return baos.toByteArray();
  }

  public void close() throws IOException {
    if (closed) {
      throw new IOException("This output stream has already been closed");
    }
    baos.close();
    closed = true;
  }

  public void flush() throws IOException {
    if (closed) {
      throw new IOException("Cannot flush a closed output stream");
    }
  }

  public void write(int b) throws IOException {
    if (closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    baos.write((byte) b);
  }

  public void write(byte b[]) throws IOException {
    if (closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    write(b, 0, b.length);
  }

  public void write(byte b[], int off, int len) throws IOException {
    if (closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    baos.write(b, off, len);
  }

  public boolean closed() {
    return (this.closed);
  }

  public void reset() {
    //noop
  }
}
