/*
 * $Id: LongRunningTest.java,v 1.1 2002-11-03 17:54:05 o_rossmueller Exp $
 *
 * (c) 2002 Oliver Rossmueller
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
 * along with tuxerra; if not, mailto:oliver@oross.net or have a look at
 * http://www.gnu.org/licenses/licenses.html#GPL
 */
package org.junitee.ejb.einstein.test;

import junit.framework.TestCase;


/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class LongRunningTest extends TestCase {

  public LongRunningTest(String s) {
    super(s);
  }


  public void testRun() {
    long time = System.currentTimeMillis();

    while(System.currentTimeMillis() - time < 15000) {
      
    }
  }
}
