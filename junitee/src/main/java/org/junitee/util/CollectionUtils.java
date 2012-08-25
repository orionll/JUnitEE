package org.junitee.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created 25.08.2012
 * @author orionll
 * 
**/
public final class CollectionUtils {
  private CollectionUtils() {

  }

  public static <T> List<T> asList(T[] a) {
    if (a == null) {
      return null;
    }

    return Arrays.<T> asList(a);
  }
}
