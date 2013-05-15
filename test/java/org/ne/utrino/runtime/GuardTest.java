package org.ne.utrino.runtime;

import static org.ne.utrino.testing.TestFactory.INT_P;
import static org.ne.utrino.testing.TestFactory.OBJ_P;
import static org.ne.utrino.testing.TestFactory.STR_P;
import static org.ne.utrino.testing.TestFactory.entry;
import static org.ne.utrino.testing.TestFactory.newHierarchy;
import static org.ne.utrino.testing.TestFactory.toValue;
import static org.ne.utrino.testing.TestFactory.withProtocol;

import org.junit.Test;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RProtocol;

import junit.framework.TestCase;

/**
 * Test of entry guards.
 */
public class GuardTest extends TestCase {

  private static final IHierarchy EMPTY_HIERARCHY = newHierarchy();

  @Test
  public void testAny() {
    Guard any = Guard.any();
    assertTrue(Guard.isMatch(any.match(toValue(0), EMPTY_HIERARCHY)));
    assertTrue(Guard.isMatch(any.match(toValue(1), EMPTY_HIERARCHY)));
    assertTrue(Guard.isMatch(any.match(toValue(null), EMPTY_HIERARCHY)));
  }

  @Test
  public void testIdentity() {
    Guard idZero = Guard.identity(toValue(0));
    Guard idNull = Guard.identity(toValue(null));
    assertTrue(Guard.isMatch(idZero.match(toValue(0), EMPTY_HIERARCHY)));
    assertFalse(Guard.isMatch(idZero.match(toValue(null), EMPTY_HIERARCHY)));
    assertFalse(Guard.isMatch(idNull.match(toValue(0), EMPTY_HIERARCHY)));
    assertTrue(Guard.isMatch(idNull.match(toValue(null), EMPTY_HIERARCHY)));
  }

  @Test
  public void testSimpleIs() {
    RProtocol sStrP = new RProtocol();
    IHierarchy hierarchy = newHierarchy(
        entry(INT_P, OBJ_P), entry(STR_P, OBJ_P), entry(sStrP, STR_P));
    Guard isInt = Guard.is(INT_P);
    Guard isObj = Guard.is(OBJ_P);
    Guard isStr = Guard.is(STR_P);
    Guard isSStr = Guard.is(sStrP);

    IValue zero = toValue(0);
    assertTrue(Guard.isMatch(isInt.match(zero, hierarchy)));
    assertTrue(Guard.isMatch(isObj.match(zero, hierarchy)));
    assertFalse(Guard.isMatch(isStr.match(zero, hierarchy)));
    assertFalse(Guard.isMatch(isSStr.match(zero, hierarchy)));

    IValue x = toValue("x");
    assertFalse(Guard.isMatch(isInt.match(x, hierarchy)));
    assertTrue(Guard.isMatch(isObj.match(x, hierarchy)));
    assertTrue(Guard.isMatch(isStr.match(x, hierarchy)));
    assertFalse(Guard.isMatch(isSStr.match(x, hierarchy)));

    IValue sStr = withProtocol(sStrP);
    assertFalse(Guard.isMatch(isInt.match(sStr, hierarchy)));
    assertTrue(Guard.isMatch(isObj.match(sStr, hierarchy)));
    assertTrue(Guard.isMatch(isStr.match(sStr, hierarchy)));
    assertTrue(Guard.isMatch(isSStr.match(sStr, hierarchy)));

    IValue nil = toValue(null);
    assertFalse(Guard.isMatch(isInt.match(nil, hierarchy)));
    assertFalse(Guard.isMatch(isObj.match(nil, hierarchy)));
    assertFalse(Guard.isMatch(isStr.match(nil, hierarchy)));
    assertFalse(Guard.isMatch(isSStr.match(nil, hierarchy)));
  }

  @Test
  public void testIsScore() {
    RProtocol sStrP = new RProtocol();
    IHierarchy h = newHierarchy(entry(STR_P, OBJ_P), entry(sStrP, STR_P));
    Guard isX = Guard.identity(toValue("x"));
    Guard isObj = Guard.is(OBJ_P);
    Guard isStr = Guard.is(STR_P);
    Guard isSStr = Guard.is(sStrP);

    IValue x = toValue("x");
    assertTrue(Guard.compareScore(isStr.match(x, h), isObj.match(x, h)) < 0);
    assertTrue(Guard.compareScore(isX.match(x, h), isStr.match(x, h)) < 0);

    IValue sStr = withProtocol(sStrP);
    assertTrue(Guard.compareScore(isStr.match(sStr, h), isObj.match(sStr, h)) < 0);
    assertTrue(Guard.compareScore(isSStr.match(sStr, h), isStr.match(sStr, h)) < 0);
  }

  @Test
  public void testMulti() {
    RProtocol intAndStrP = new RProtocol();
    IHierarchy h = newHierarchy(entry(intAndStrP, STR_P, INT_P));
    Guard isStr = Guard.is(STR_P);
    Guard isInt = Guard.is(INT_P);

    IValue intAndStr = withProtocol(intAndStrP);
    assertTrue(Guard.compareScore(isStr.match(intAndStr, h), isInt.match(intAndStr, h)) == 0);
  }

}
