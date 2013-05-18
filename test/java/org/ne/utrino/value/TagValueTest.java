package org.ne.utrino.value;

import static org.ne.utrino.value.RInteger.of;

import org.junit.Test;

import junit.framework.TestCase;

public class TagValueTest extends TestCase {

  @Test
  public void testComparison() {
    assertTrue(of(1).compareTo(of(1)) == 0);
    assertTrue(of(1).compareTo(of(2)) < 0);
    assertTrue(of(2).compareTo(of(1)) > 0);
  }

}
