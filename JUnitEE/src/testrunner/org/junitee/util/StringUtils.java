/*
 * $Id: StringUtils.java,v 1.2 2002-09-20 20:39:41 o_rossmueller Exp $
 */
package org.junitee.util;


/**
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.2 $
 * @since 1.5
 */
public class StringUtils {
  /**
   * This method converts texts to be displayed on
   * html-page. Following conversion are done
   * "<" => "&lt;" , ">" => "&gt;" and "&" => "&amp;"
   * @author Kaarle Kaila
   * @since 10.10.2001
   *
   * And replaced \n with html breaks - jeff
   */
  public static String htmlText(String text) {
    StringBuffer sb = new StringBuffer();
    char c;
    if (text == null) return "";
    for (int i = 0; i < text.length(); i++) {
      c = text.charAt(i);
      switch (c) {
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '\"':
          sb.append("&quot;");
          break;
        case '\n':
          sb.append("<br>");
          break;
        default:
          sb.append(c);
      }
    }
    return sb.toString();
  }


  /**
   * This method converts texts to be used in an
   * xml document. Following conversion are done
   * "<" => "&lt;" , ">" => "&gt;" and "&" => "&amp;"
   */
  public static String xmlText(String text) {
    StringBuffer sb = new StringBuffer();
    char c;
    if (text == null) return "";
    for (int i = 0; i < text.length(); i++) {
      c = text.charAt(i);
      switch (c) {
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '\"':
          sb.append("&quot;");
          break;
        default:
          sb.append(c);
      }
    }
    return sb.toString();
  }
}
