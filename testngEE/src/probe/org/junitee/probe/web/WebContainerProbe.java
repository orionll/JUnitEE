/*
 * $Id: WebContainerProbe.java,v 1.1 2005-10-19 23:36:15 o_rossmueller Exp $
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
package org.junitee.probe.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 */
public interface WebContainerProbe extends Serializable {

  void probeContainer(HttpSession session, HttpServletRequest request);
}
