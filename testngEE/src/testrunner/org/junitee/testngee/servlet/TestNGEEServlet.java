/*
 * $Id: TestNGEEServlet.java,v 1.1 2005-10-18 23:29:39 o_rossmueller Exp $
 *
 * Copyright 2005 Oliver Rossmueller
 *
 * This file is part of testngEE.
 *
 * testngEE is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * testngEE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with testngEE; if not, mailto:oliver@tuxerra.com or have a look at
 * http://www.gnu.org/licenses/licenses.html#GPL
 */
package org.junitee.testngee.servlet;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import org.testng.TestNG;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

/**
 * Servlet used by the TestNGEE ant task to delegate test execution to.
 *
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 */
public class TestNGEEServlet extends HttpServlet {

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    DiskFileUpload upload = new DiskFileUpload();
    List<File> xmlFiles = new ArrayList<File>();

    try {
      List items = upload.parseRequest(request);

      for (Iterator iterator = items.iterator(); iterator.hasNext();) {
        FileItem item = (FileItem) iterator.next();

        if (item.isFormField()) {

        } else {
          // it's an uploaded xml file
          File file = File.createTempFile("testng", ".xml");
          item.write(file);
          xmlFiles.add(file);
        }
      }
      File outdir = File.createTempFile("output", "");
      outdir.delete();
      outdir.mkdirs();

      String[] args = new String[xmlFiles.size() + 2];
      args[0] = "-d";
      args[1] = outdir.getAbsolutePath();

      int i = 2;

      for (Iterator<File> iterator = xmlFiles.iterator(); iterator.hasNext();) {
        File file = iterator.next();
        args[i++] = file.getAbsolutePath();
      }

      TestNG testNG = TestNG.privateMain(args, null);

      zipOutputDir(outdir, response.getOutputStream());

      cleanup(xmlFiles, outdir);
    } catch (FileUploadException e) {
      throw new ServletException(e);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void cleanup(List<File> xmlFiles, File outdir) {
    for (Iterator<File> iterator = xmlFiles.iterator(); iterator.hasNext();) {
      File file = iterator.next();
      file.delete();
    }

    File[] files = outdir.listFiles();

    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      file.delete();
    }
    outdir.delete();
  }

  private void zipOutputDir(File outdir, ServletOutputStream outputStream) throws IOException {
    ZipOutputStream out = new ZipOutputStream(outputStream);
    File[] files = outdir.listFiles();

    out.setMethod(ZipOutputStream.DEFLATED);

    for (int i = 0; i < files.length; i++) {
      File file = files[i];

      if (! file.isDirectory()) {
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipEntry.setSize(file.length());
        zipEntry.setTime(file.lastModified());

        out.putNextEntry(zipEntry);

        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int r;

        while ((r = in.read(buffer)) > 0) {
          out.write(buffer, 0, r);
        }
        in.close();
        out.closeEntry();
      }
    }
    out.close();
  }
}
