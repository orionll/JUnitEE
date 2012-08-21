/*
 * $Id: TestProbe.java,v 1.1 2005-10-19 23:36:15 o_rossmueller Exp $
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
package org.junitee.testngee.example.test;

import org.junitee.probe.util.ProbeDecoder;
import org.junitee.probe.util.ProbeEncoder;
import org.junitee.probe.web.WebContainerProbeFilter;
import org.junitee.testngee.example.probe.ProbeSessionAfter;
import org.junitee.testngee.example.probe.ProbeSessionBefore;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 */
public class TestProbe {


  @Test
  public void testProbe() throws IOException, ClassNotFoundException {
    ProbeSessionBefore probeBefore = new ProbeSessionBefore();
    ProbeSessionAfter probeAfter = new ProbeSessionAfter();

    URL url = new URL(System.getProperty("test.url"));
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoInput(true);
    connection.setRequestMethod("GET");
    connection.setRequestProperty(WebContainerProbeFilter.HEADER_PROBE_BEFORE, ProbeEncoder.encode(probeBefore));
    connection.setRequestProperty(WebContainerProbeFilter.HEADER_PROBE_AFTER, ProbeEncoder.encode(probeAfter));

    Object content = connection.getContent();
    probeBefore = (ProbeSessionBefore) ProbeDecoder.decode(connection.getHeaderField(WebContainerProbeFilter.HEADER_PROBE_BEFORE));
    probeAfter =  (ProbeSessionAfter) ProbeDecoder.decode(connection.getHeaderField(WebContainerProbeFilter.HEADER_PROBE_AFTER));
    assertTrue(probeBefore.isSuccess());
    assertTrue(probeAfter.isSuccess());
    assertNotNull(content);
  }
}
