package org.ne.utrino.testing;

import org.ne.utrino.util.Assert;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RInteger;
import org.ne.utrino.value.RString;

/**
 * Convenience factory methods used in tests.
 */
public class TestFactory {

  /**
   * Converts an object to a tag value. For instance, wraps integers and strings
   * in objects.
   */
  public static ITagValue toTag(Object obj) {
    if (obj instanceof Integer) {
      return RInteger.newInt((Integer) obj);
    } else if (obj instanceof String) {
      return new RString((String) obj);
    } else {
      Assert.that(obj instanceof ITagValue);
      return (ITagValue) obj;
    }
  }

  /**
   * Converts an object to a value. For instance, wraps integers and strings in
   * neutrino objects.
   */
  public static IValue toValue(Object obj) {
    if (obj instanceof IValue) {
      return (IValue) obj;
    } else {
      return toTag(obj);
    }
  }

}
