package org.ne.utrino.value;

import static org.ne.utrino.value.RInteger.newInt;

import org.junit.Test;

import junit.framework.TestCase;

public class TagValueTest extends TestCase {

  @Test
  public void testComparison() {
    assertTrue(newInt(1).compareTo(newInt(1)) == 0);
    assertTrue(newInt(1).compareTo(newInt(2)) < 0);
    assertTrue(newInt(2).compareTo(newInt(1)) > 0);
  }

}
