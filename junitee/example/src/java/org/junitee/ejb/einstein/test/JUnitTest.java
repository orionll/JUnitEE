/*
 * $Id: JUnitTest.java,v 1.1 2003-07-27 22:27:14 o_rossmueller Exp $
 *
 * Copyright (c) 2003 Oliver Rossmueller
 *
 * This file is part of welofunc.
 *
 * welofunc is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * welofunc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with welofunc; if not, mailto:oliver@welofunc.com or have a look at
 * http://www.gnu.org/licenses/licenses.html#GPL
 */
package org.junitee.ejb.einstein.test;


import junit.framework.TestCase;


/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class JUnitTest extends TestCase{

  public JUnitTest(String s) {
    super(s);
  }

  public void test() {
    System.out.println("test");
  }
}
