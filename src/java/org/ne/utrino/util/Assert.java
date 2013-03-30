package org.ne.utrino.util;

import java.util.Objects;



/**
 * Assertion library to avoid using Java's which is difficult to
 * control.
 */
public class Assert {

  /**
   * Set this to false to disable the most expensive assertions.
   */
  public static final boolean enableExpensiveAssertions = true;

  /**
   * Fails if the given value is false.
   */
  public static void that(boolean value) {
    if (!value) {
      throw new AssertionError();
    }
  }

  /**
   * Fails if the given value is null.
   */
  public static <T> T notNull(T obj) {
    that(obj != null);
    return obj;
  }

  /**
   * Fails if the given value is not null.
   */
  public static <T> T isNull(T obj) {
    that(obj == null);
    return obj;
  }

  /**
   * Fails if the two objects are not .equals.
   */
  public static void equals(Object a, Object b) {
    that(Objects.equals(a, b));
  }

}
