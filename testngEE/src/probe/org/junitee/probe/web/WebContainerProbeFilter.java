/*
 * $Id: WebContainerProbeFilter.java,v 1.1 2005-10-19 23:36:15 o_rossmueller Exp $
 *
 * Copyright 2005 Oliver Rossmueller
 *
 * This file is part of tuxerra.
 *
 * tuxerra is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * tuxerra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with welofunc; if not, mailto:oliver@tuxerra.com or have a look at
 * http://www.gnu.org/licenses/licenses.html#GPL
 */
package org.junitee.probe.web;

import org.junitee.probe.util.ProbeDecoder;
import org.junitee.probe.util.ProbeEncoder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 */
public class WebContainerProbeFilter implements Filter {

  public static final String HEADER_PROBE_BEFORE = "WebContainerProbeBefore";
  public static final String HEADER_PROBE_AFTER = "WebContainerProbeAfter";

  public void init(FilterConfig filterConfig) throws ServletException {

  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    if (servletRequest instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      String headerBefore = request.getHeader(HEADER_PROBE_BEFORE);
      String headerAfter = request.getHeader(HEADER_PROBE_AFTER);

      if (headerBefore != null) {
        try {
          Object object = ProbeDecoder.decode(headerBefore);

          if (object instanceof WebContainerProbe) {
            WebContainerProbe probe = (WebContainerProbe) object;
            probe.probeContainer(request.getSession(false), request);
            String probeReturn = ProbeEncoder.encode(probe);
            response.setHeader(HEADER_PROBE_BEFORE, probeReturn);
          }
        } catch (ClassNotFoundException e) {
          // failed to deserialize probe
          System.err.println("Failed to deserialize probe: " + e.getMessage());
        }
      }


      if (headerAfter != null) {
        try {
          Object object = ProbeDecoder.decode(headerAfter);

          if (object instanceof WebContainerProbe) {
            WebContainerProbe probe = (WebContainerProbe) object;

            BufferedResponseWrapper responseWrapper = new BufferedResponseWrapper(response);
            filterChain.doFilter(servletRequest, responseWrapper);

            responseWrapper.finishResponse();
            probe.probeContainer(request.getSession(false), request);
            String probeReturn = ProbeEncoder.encode(probe);
            response.setHeader(HEADER_PROBE_AFTER, probeReturn);
            response.getWriter().print(responseWrapper.getContent());
          } else {
            filterChain.doFilter(servletRequest, servletResponse);
          }
        } catch (ClassNotFoundException e) {
          // failed to deserialize probe
          System.err.println("Failed to deserialize probe: " + e.getMessage());
          filterChain.doFilter(servletRequest, servletResponse);
        }
      } else {
        filterChain.doFilter(servletRequest, servletResponse);
      }
    }
  }

  public void destroy() {

  }
}
