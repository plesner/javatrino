package org.ne.utrino.runtime;

import static org.ne.utrino.runtime.Signature.MatchResult.GUARD_REJECTED;
import static org.ne.utrino.runtime.Signature.MatchResult.MATCH;
import static org.ne.utrino.runtime.Signature.MatchResult.MISSING_ARGUMENTS;
import static org.ne.utrino.runtime.Signature.MatchResult.UNEXPECTED_ARGUMENT;
import static org.ne.utrino.testing.TestFactory.toTag;
import static org.ne.utrino.testing.TestFactory.toValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.ne.utrino.util.Pair;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;

import junit.framework.TestCase;

public class SignatureTest extends TestCase {

  /**
   * Creates a tagged parameter.
   */
  private static Signature.ParameterBuilder newParam(Guard guard, Object... tags) {
    Signature.ParameterBuilder builder = new Signature.ParameterBuilder(guard);
    for (Object tag : tags)
      builder.addTag(toTag(tag));
    return builder;
  }

  /**
   * Combines a set of parameters into a signature.
   */
  private static Signature newSig(Signature.ParameterBuilder... params) {
    Signature.Builder builder = Signature.newBuilder();
    for (Signature.ParameterBuilder param : params)
      builder.addParameter(param);
    return builder.build();
  }

  /**
   * Creates an argument pair.
   */
  private static Pair<ITagValue, IValue> a(Object tag, Object value) {
    return Pair.of(toTag(tag), toValue(value));
  }

  /**
   * Creates an invocation from alternating tags and values.
   */
  @SafeVarargs
  private static IInvocation newInvoke(Pair<ITagValue, IValue>... pairs) {
    final List<Pair<ITagValue, IValue>> args = Arrays.asList(pairs);
    Collections.sort(args, Pair.<ITagValue, IValue>firstComparator());
    return new IInvocation() {
      @Override
      public IValue getValue(int index) {
        return args.get(index).getSecond();
      }
      @Override
      public ITagValue getTag(int index) {
        return args.get(index).getFirst();
      }
      @Override
      public int getEntryCount() {
        return args.size();
      }
    };
  }

  @Test
  public void testSimpleMatching() {
    Signature sig = newSig(
        newParam(Guard.any(), 0),
        newParam(Guard.any(), 1));
    assertEquals(MATCH, sig.match(newInvoke(a(0, "foo"), a(1, "bar"))));
    assertEquals(UNEXPECTED_ARGUMENT, sig.match(newInvoke(a(0, "foo"), a(1, "bar"),
        a(2, "baz"))));
    assertEquals(MISSING_ARGUMENTS, sig.match(newInvoke(a(0, "foo"))));
    assertEquals(MISSING_ARGUMENTS, sig.match(newInvoke(a(1, "bar"))));
    assertEquals(UNEXPECTED_ARGUMENT, sig.match(newInvoke(a(2, "baz"))));
    assertEquals(MISSING_ARGUMENTS, sig.match(newInvoke()));
  }

  @Test
  public void testSimpleGuardMatching() {
    Signature sig = newSig(
        newParam(Guard.identity(toValue("foo")), 0),
        newParam(Guard.any(), 1));
    assertEquals(MATCH, sig.match(newInvoke(a(0, "foo"), a(1, "bar"))));
    assertEquals(MATCH, sig.match(newInvoke(a(0, "foo"), a(1, "boo"))));
    assertEquals(GUARD_REJECTED, sig.match(newInvoke(a(0, "fop"), a(1, "boo"))));
  }

}
