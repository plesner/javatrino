package org.ne.utrino.util;

import static org.ne.utrino.testing.LazyAssert.assertLazyEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.ne.utrino.testing.LazyTest;
import org.ne.utrino.testing.LazyTestRunner;

import junit.framework.TestCase;

@RunWith(LazyTestRunner.class)
public class PromiseTest extends TestCase {

  @LazyTest
  public void testSimpleThen() {
    Promise<Integer> p = Promise.newEmpty();
    Promise<Integer> p2 = p.then(new IFunction<Integer, Integer>() {
      @Override
      public Integer call(Integer arg) {
        return arg.intValue() + 1;
      }
    });
    assertLazyEquals(2, p2);
    p.fulfill(1);
  }

  @LazyTest
  public void testMultiFulfill() {
    Promise<Integer> p = Promise.newEmpty();
    p.fulfill(3);
    p.fulfill(4);
    assertLazyEquals(3, p);
  }

  @LazyTest
  public void testEagerJoin() {
    Promise<List<Integer>> joined = Promise.join(Promise.of(1), Promise.of(2));
    assertLazyEquals(Arrays.asList(1, 2), joined);
  }

  @LazyTest
  public void testLazyJoin() {
    Promise<Integer> first = Promise.newEmpty();
    Promise<Integer> second = Promise.newEmpty();
    Promise<List<Integer>> joined = Promise.join(first, second);
    assertLazyEquals(Arrays.asList(8, 7), joined);
    second.fulfill(7);
    first.fulfill(8);
  }

  @LazyTest
  public void testEmptyJoin() {
    Promise<List<Object>> joined = Promise.join();
    assertTrue(joined.isResolved());
    assertEquals(Arrays.asList(), joined.getValue());
  }

}
