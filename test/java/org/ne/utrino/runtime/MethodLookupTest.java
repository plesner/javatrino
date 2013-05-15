package org.ne.utrino.runtime;

import static org.ne.utrino.runtime.MethodLookup.JoinStatus.AMBIGUOUS;
import static org.ne.utrino.runtime.MethodLookup.JoinStatus.BETTER;
import static org.ne.utrino.runtime.MethodLookup.JoinStatus.EQUAL;
import static org.ne.utrino.runtime.MethodLookup.JoinStatus.WORSE;
import static org.ne.utrino.testing.TestFactory.OBJ_P;
import static org.ne.utrino.testing.TestFactory.arg;
import static org.ne.utrino.testing.TestFactory.entry;
import static org.ne.utrino.testing.TestFactory.ints;
import static org.ne.utrino.testing.TestFactory.newHierarchy;
import static org.ne.utrino.testing.TestFactory.newInvocation;
import static org.ne.utrino.testing.TestFactory.newSignature;
import static org.ne.utrino.testing.TestFactory.param;
import static org.ne.utrino.testing.TestFactory.toTag;
import static org.ne.utrino.testing.TestFactory.withProtocol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.ne.utrino.runtime.MethodLookup.JoinStatus;
import org.ne.utrino.util.Factory;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RMethod;
import org.ne.utrino.value.RProtocol;

import junit.framework.TestCase;

public class MethodLookupTest extends TestCase {

  private static void checkJoin(JoinStatus status, int[] expected, int[] a, int[] b) {
    int[] bBefore = b.clone();
    assertEquals(status, MethodLookup.join(a, b, expected.length));
    assertTrue(Arrays.equals(expected, a));
    assertTrue(Arrays.equals(bBefore, b));
  }

  @Test
  public void testJoin() {
    checkJoin(EQUAL, ints(), ints(), ints());
    checkJoin(EQUAL, ints(1), ints(1), ints(1));
    checkJoin(AMBIGUOUS, ints(0, 0), ints(0, 1), ints(1, 0));
    checkJoin(BETTER, ints(1, 2), ints(2, 3), ints(1, 2));
    checkJoin(BETTER, ints(0, 0), ints(5, 5), ints(0, 0));
    checkJoin(WORSE, ints(1, 2), ints(1, 2), ints(2, 3));
    checkJoin(WORSE, ints(0, 0), ints(0, 0), ints(5, 5));
  }

  private static RMethod newMethod(Guard a, Guard b, Guard c) {
    return new RMethod(newSignature(param(a, toTag(0)), param(b, toTag(1)),
        param(c, toTag(2))));
  }

  private static IInvocation newArgs(IValue a, IValue b, IValue c) {
    return newInvocation(arg(0, a), arg(1, b), arg(2, c));
  }

  private static void checkLookup(RMethod expected, IValue a, IValue b, IValue c,
      Collection<RMethod> methods, IHierarchy hierarchy) {
    MethodLookup lookup = new MethodLookup(3);
    lookup.findMethod(newArgs(a, b, c), methods, hierarchy);
    assertEquals(expected, lookup.getMethod());
  }

  @SuppressWarnings("serial")
  @Test
  public void testDensePerfectLookup() {
    // D <: C <: B <: A <: Object
    final RProtocol aP = new RProtocol("A");
    final RProtocol bP = new RProtocol("B");
    final RProtocol cP = new RProtocol("C");
    final RProtocol dP = new RProtocol("D");
    IHierarchy hierarchy = newHierarchy(entry(dP, cP), entry(cP, bP),
        entry(bP, aP), entry(aP, OBJ_P));
    Map<String, RProtocol> protocols = new HashMap<String, RProtocol>() {{
      put("a", aP);
      put("b", bP);
      put("c", cP);
      put("d", dP);
    }};

    final IValue a = withProtocol(aP);
    final IValue b = withProtocol(bP);
    final IValue c = withProtocol(cP);
    final IValue d = withProtocol(dP);
    Map<String, IValue> values = new HashMap<String, IValue>() {{
      put("a", a);
      put("b", b);
      put("c", c);
      put("d", d);
    }};

    // Build a method for each combination of parameter types.
    List<String> letters = Arrays.asList("a", "b", "c", "d");
    Map<String, RMethod> methodMap = Factory.newTreeMap();
    for (String first : letters) {
      for (String second : letters) {
        for (String third : letters) {
          RMethod method = newMethod(Guard.is(protocols.get(first)),
              Guard.is(protocols.get(second)), Guard.is(protocols.get(third)));
          methodMap.put(first + second + third, method);
        }
      }
    }

    // Look up for each combination of argument types and check that the unique
    // matching method comes out. Do this for a number of random permutations
    // of the list of methods.
    List<RMethod> methods = Factory.newArrayList();
    methods.addAll(methodMap.values());
    Random random = new Random(32143);
    for (int i = 0; i < 10; i++) {
      for (String first : letters) {
        for (String second : letters) {
          for (String third : letters) {
            RMethod expected = methodMap.get(first + second + third);
            checkLookup(expected, values.get(first), values.get(second), values.get(third),
                methodMap.values(), hierarchy);
          }
        }
      }
      Collections.shuffle(methods, random);
    }
  }

}
