package org.ne.utrino.testing;

import org.ne.utrino.util.Promise;

import junit.framework.Assert;

/**
 * Assertions that can be used to work with promises.
 */
public class LazyAssert extends Assert {

  /**
   * A listener for lazy events.
   */
  public interface IListener {

    /**
     * Instructs the framework to wait for the given promise to resolve before
     * continuing.
     */
    public void waitForPromise(Promise<?> that);

    /**
     * Invoke the given callback when all promises we've been asked to wait
     * for have been resolved.
     */
    public void whenResolved(Throwable location, Runnable callback);

  }

  private static final ThreadLocal<IListener> currentListener = new ThreadLocal<IListener>();

  /**
   * Causes the test to wait for these promises and once they're resolved checks
   * that their values are equal.
   */
  public static <T> void assertLazyEquals(final Promise<? extends T> a,
      final Promise<? extends T> b) {
    getListener().waitForPromise(a);
    getListener().waitForPromise(b);
    getListener().whenResolved(new Throwable(), new Runnable() {
      @Override
      public void run() {
        Assert.assertEquals(a.getValue(), b.getValue());
      }
    });
  }

  /**
   * Waits for the given promise to resolve and once it has, checks that its
   * value is equal to the given expected value.
   */
  public static <T> void assertLazyEquals(T a, Promise<? extends T> b) {
    assertLazyEquals(Promise.of(a), b);
  }

  private static IListener getListener() {
    return currentListener.get();
  }

  /**
   * Invokes the given callback after setting the given listener as the current
   * test listener this class will report to. Removes it as the test listener
   * afterwards, before returning.
   */
  public static void withListener(IListener listener, Runnable callback) {
    org.ne.utrino.util.Assert.isNull(currentListener.get());
    try {
      currentListener.set(listener);
      callback.run();
    } finally {
      currentListener.set(null);
    }
  }

}
