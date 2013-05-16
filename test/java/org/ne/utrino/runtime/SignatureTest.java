package org.ne.utrino.runtime;

import static org.ne.utrino.runtime.Signature.MatchResult.EXTRA_MATCH;
import static org.ne.utrino.runtime.Signature.MatchResult.GUARD_REJECTED;
import static org.ne.utrino.runtime.Signature.MatchResult.MATCH;
import static org.ne.utrino.runtime.Signature.MatchResult.MISSING_ARGUMENT;
import static org.ne.utrino.runtime.Signature.MatchResult.REDUNDANT_ARGUMENT;
import static org.ne.utrino.runtime.Signature.MatchResult.UNEXPECTED_ARGUMENT;
import static org.ne.utrino.testing.TestFactory.arg;
import static org.ne.utrino.testing.TestFactory.newInvocation;
import static org.ne.utrino.testing.TestFactory.newSignature;
import static org.ne.utrino.testing.TestFactory.param;
import static org.ne.utrino.testing.TestFactory.toValue;

import org.junit.Test;
import org.ne.utrino.runtime.Signature.MatchResult;
import org.ne.utrino.util.Pair;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;

import junit.framework.TestCase;

public class SignatureTest extends TestCase {

  @SafeVarargs
  public static void assertMatch(MatchResult expected, Signature sig,
      Pair<ITagValue, IValue>... pairs) {
    assertEquals(expected, sig.match(newInvocation(pairs), null, new int[pairs.length]));
  }

  @Test
  public void testSimpleMatching() {
    Signature sig = newSignature(
        false,
        param(Guard.any(), false, 0),
        param(Guard.any(), false, 1));
    assertMatch(MATCH, sig, arg(0, "foo"), arg(1, "bar"));
    assertMatch(UNEXPECTED_ARGUMENT, sig, arg(0, "foo"), arg(1, "bar"), arg(2, "baz"));
    assertMatch(MISSING_ARGUMENT, sig, arg(0, "foo"));
    assertMatch(MISSING_ARGUMENT, sig, arg(1, "bar"));
    assertMatch(UNEXPECTED_ARGUMENT, sig, arg(2, "baz"));
    assertMatch(MISSING_ARGUMENT, sig);
  }

  @Test
  public void testSimpleGuardMatching() {
    Signature sig = newSignature(
        false,
        param(Guard.identity(toValue("foo")), false, 0),
        param(Guard.any(), false, 1));
    assertMatch(MATCH, sig, arg(0, "foo"), arg(1, "bar"));
    assertMatch(MATCH, sig, arg(0, "foo"), arg(1, "boo"));
    assertMatch(GUARD_REJECTED, sig, arg(0, "fop"), arg(1, "boo"));
  }

  @Test
  public void testMultiTagMatching() {
    Signature sig = newSignature(
        false,
        param(Guard.any(), false, 0, "x"),
        param(Guard.any(), false, 1, "y"));
    assertMatch(MATCH, sig, arg(0, "foo"), arg(1, "bar"));
    assertMatch(MATCH, sig, arg(0, "foo"), arg("y", "bar"));
    assertMatch(MATCH, sig, arg(1, "bar"), arg("x", "foo"));
    assertMatch(MATCH, sig, arg("x", "foo"), arg("y", "bar"));
    assertMatch(REDUNDANT_ARGUMENT, sig, arg(0, "foo"), arg("x", "foo"));
    assertMatch(REDUNDANT_ARGUMENT, sig, arg(1, "foo"), arg("y", "foo"));
  }

  @Test
  public void testExtraArgs() {
    Signature sig = newSignature(
        true,
        param(Guard.any(), false, 0, "x"),
        param(Guard.any(), false, 1, "y"));
    assertMatch(MATCH, sig, arg(0, "foo"), arg(1, "bar"));
    assertMatch(MATCH, sig, arg(0, "foo"), arg("y", "bar"));
    assertMatch(MATCH, sig, arg(1, "bar"), arg("x", "foo"));
    assertMatch(MATCH, sig, arg("x", "foo"), arg("y", "bar"));
    assertMatch(REDUNDANT_ARGUMENT, sig, arg(0, "foo"), arg("x", "foo"));
    assertMatch(REDUNDANT_ARGUMENT, sig, arg(1, "foo"), arg("y", "foo"));
    assertMatch(EXTRA_MATCH, sig, arg(0, "foo"), arg(1, "bar"), arg(2, "baz"));
    assertMatch(EXTRA_MATCH, sig, arg(0, "foo"), arg(1, "bar"), arg(2, "baz"),
        arg(3, "quux"));
    assertMatch(MISSING_ARGUMENT, sig, arg(1, "bar"), arg(2, "baz"));
    assertMatch(MISSING_ARGUMENT, sig, arg(0, "foo"), arg(2, "baz"));
    assertMatch(EXTRA_MATCH, sig, arg(0, "foo"), arg(1, "bar"), arg("z", "baz"));
  }

}
