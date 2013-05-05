package org.ne.utrino.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * A deferred value.
 */
public class Promise<T> {

  private enum State {
    EMPTY,
    FAILED,
    SUCCEEDED
  }

  private final Executor callbackExecutor;
  private State state = State.EMPTY;
  private T value;
  private Throwable error;
  private List<ICallback<? super T>> callbacks;

  private Promise(Executor callbackExecutor) {
    this.callbackExecutor = callbackExecutor;
  }

  /**
   * Sets the value of this promise. If it has already been resolved
   * nothing happens.
   */
  public void fulfill(T value) {
    if (isResolved())
      return;
    this.value = value;
    this.state = State.SUCCEEDED;
    firePendingCallbacks();
  }

  /**
   * Fails this promise. If it has already been resolved nothing
   * happens.
   */
  public void fail(Throwable error) {
    if (isResolved())
      return;
    this.error = error;
    this.state = State.FAILED;
    firePendingCallbacks();
  }

  /**
   * Has this promise been given its value?
   */
  public boolean isResolved() {
    return state != State.EMPTY;
  }

  /**
   * Returns true if this promise has been successfully resolved.
   */
  public boolean hasSucceeded() {
    return state == State.SUCCEEDED;
  }

  /**
   * Adds a listener to the set that should be called when this promise
   * is resolved. If it has already been resolved the callback is
   * called immediately.
   */
  public void onResolved(ICallback<? super T> callback) {
    if (isResolved()) {
      fireCallback(callback);
    } else {
      if (callbacks == null)
        callbacks = Factory.newArrayList();
      callbacks.add(callback);
    }
  }

  /**
   * Utility that keeps track of state while joining promises.
   */
  private static class Joiner<T> {

    private final T[] values;
    private Promise<List<T>> result;
    private int remaining;

    public Joiner(Promise<List<T>> result, int count) {
      this.result = result;
      this.values = (T[]) new Object[count];
      this.remaining = count;
    }

    private void onSuccess(int index, T value) {
      if (result == null)
        return;
      values[index] = value;
      remaining--;
      if (remaining == 0) {
        result.fulfill(Arrays.asList(values));
        result = null;
      }
    }

    private void onFailure(int index, Throwable error) {
      if (result == null)
        return;
      result.fail(error);
      result = null;
    }

    /**
     * Returns a callback that is suitable for handling the resolution of the
     * index'th promise being joined.
     */
    public ICallback<T> getResolver(final int index) {
      return new ICallback<T>() {
        @Override
        public void onSuccess(T value) {
          Joiner.this.onSuccess(index, value);
        }
        @Override
        public void onFailure(Throwable error) {
          Joiner.this.onFailure(index, error);
        }
      };
    }

  }

  /**
   * Returns a new promise that resolves when all the given promises have
   * resolved, and whose value will be a list of the values of those promises.
   */
  public static <T> Promise<List<T>> join(Promise<T>... promises) {
    if (promises.length == 0)
      return Promise.of(Collections.<T>emptyList());
    Promise<List<T>> result = Promise.newEmpty();
    Joiner<T> joiner = new Joiner<T>(result, promises.length);
    for (int i = 0; i < promises.length; i++)
      promises[i].onResolved(joiner.getResolver(i));
    return result;
  }

  /**
   * Clears and fires the list of callbacks.
   */
  private void firePendingCallbacks() {
    Assert.that(isResolved());
    if (callbacks == null)
      return;
    List<ICallback<? super T>> pending = callbacks;
    callbacks = null;
    for (ICallback<? super T> callback : pending)
      fireCallback(callback);
  }

  /**
   * Fires a single callback.
   */
  private void fireCallback(final ICallback<? super T> callback) {
    if (state == State.SUCCEEDED) {
      callbackExecutor.execute(new Runnable() {
        public void run() {
          callback.onSuccess(value);
        }
      });
    } else {
      callbackExecutor.execute(new Runnable() {
        public void run() {
          callback.onFailure(error);
        }
      });
    }
  }

  /**
   * Creates a new empty promise.
   */
  public static <T> Promise<T> newEmpty(Executor callbackExecutor) {
    return new Promise<T>(callbackExecutor);
  }

  /**
   * Creates a new empty promise.
   */
  public static <T> Promise<T> newEmpty() {
    return new Promise<T>(Executors.sameThread());
  }


  /**
   * Returns the value of this promise, failing if it has not succeeded.
   */
  public T getValue() {
    Assert.equals(state, State.SUCCEEDED);
    return this.value;
  }

  /**
   * Creates a new promise with a fixed value.
   */
  public static <T> Promise<T> of(T value) {
    Promise<T> result = Promise.newEmpty();
    result.fulfill(value);
    return result;
  }

  /**
   * Resolve the given promise when this one is resolved. Returns this
   * promise.
   */
  public Promise<T> forwardTo(final Promise<? super T> target) {
    this.onResolved(new ICallback<T>() {
      @Override
      public void onSuccess(T value) {
        target.fulfill(value);
      }
      @Override
      public void onFailure(Throwable error) {
        target.fail(error);
      }
    });
    return this;
  }

  /**
   * Returns a new promise that is the result of applying the given
   * filter to the value of this promise. Errors are passed through
   * unchanged.
   */
  public <S> Promise<S> then(final IFunction<? super T, ? extends S> filter) {
    final Promise<S> result = newEmpty();
    onResolved(new ICallback<T>() {
      @Override
      public void onSuccess(T value) {
        result.fulfill(filter.call(value));
      }
      @Override
      public void onFailure(Throwable error) {
        result.fail(error);
      }
    });
    return result;
  }

  /**
   * Schedules the given action to be performed if this promise fails.
   * Returns this promise.
   */
  public Promise<T> onFail(final IThunk<? super Throwable> action) {
    onResolved(new ICallback<T>() {
      @Override
      public void onSuccess(T value) {
        // ignore
      }
      @Override
      public void onFailure(Throwable error) {
        action.call(error);
      }
    });
    return this;
  }

  /**
   * When this promise is fulfilled the given filter function is called and
   * the result is forwarded to a promise which this function returns.
   */
  public <S> Promise<S> lazyThen(final IFunction<? super T, ? extends Promise<? extends S>> filter) {
    final Promise<S> result = newEmpty();
    onResolved(new ICallback<T>() {
      @Override
      public void onSuccess(T value) {
        filter.call(value).forwardTo(result);
      }
      @Override
      public void onFailure(Throwable error) {
        result.fail(error);
      }
    });
    return result;
  }

}
