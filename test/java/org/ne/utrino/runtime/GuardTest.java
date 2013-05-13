package org.ne.utrino.runtime;

import org.junit.Test;
import org.ne.utrino.value.RInteger;
import org.ne.utrino.value.RNull;

import junit.framework.TestCase;

/**
 * Test of entry guards.
 */
public class GuardTest extends TestCase {

  @Test
  public void testAny() {
    Guard any = Guard.any();
    assertTrue(Guard.isMatch(any.match(new RInteger(0))));
    assertTrue(Guard.isMatch(any.match(new RInteger(1))));
    assertTrue(Guard.isMatch(any.match(RNull.get())));
  }

  @Test
  public void testIdentity() {
    Guard idZero = Guard.identity(new RInteger(0));
    Guard idNull = Guard.identity(RNull.get());
    assertTrue(Guard.isMatch(idZero.match(new RInteger(0))));
    assertFalse(Guard.isMatch(idZero.match(RNull.get())));
    assertFalse(Guard.isMatch(idNull.match(new RInteger(0))));
    assertTrue(Guard.isMatch(idNull.match(RNull.get())));
  }

}
