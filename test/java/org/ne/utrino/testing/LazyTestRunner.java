package org.ne.utrino.testing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.ne.utrino.util.Exceptions;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.ICallback;
import org.ne.utrino.util.Pair;
import org.ne.utrino.util.Promise;

import junit.framework.TestCase;

public class LazyTestRunner extends Runner {

  private final Class<? extends TestCase> klass;
  private final List<Pair<Description, Method>> methods;
  private final Description desc;

  public LazyTestRunner(Class<? extends TestCase> klass) {
    this.klass = klass;
    this.methods = createMethodList();
    this.desc = Description.createSuiteDescription(klass);
    for (Pair<Description, Method> pair : methods)
      this.desc.addChild(pair.getFirst());
  }

  @Override
  public Description getDescription() {
    return this.desc;
  }

  private List<Pair<Description, Method>> createMethodList() {
    List<Pair<Description, Method>> result = Factory.newArrayList();
    for (Method method : klass.getDeclaredMethods()) {
      if (method.getAnnotation(LazyTest.class) != null) {
        Description desc = Description.createTestDescription(klass, method.getName());
        result.add(Pair.of(desc, method));
      }
    }
    return result;
  }

  @Override
  public void run(RunNotifier notifier) {
    for (Pair<Description, Method> pair : this.methods) {
      final TestCase testCase;
      try {
        testCase = this.klass.newInstance();
      } catch (IllegalAccessException iae) {
        throw Exceptions.propagate(iae);
      } catch (InstantiationException ie) {
        throw Exceptions.propagate(ie);
      }
      Description desc = pair.getFirst();
      notifier.fireTestStarted(desc);
      final Method method = pair.getSecond();
      final Object[] result = {null};
      LazyTestListener listener = new LazyTestListener(notifier, desc);
      LazyAssert.withListener(listener, new Runnable() {
        @Override
        public void run() {
          try {
            result[0] = method.invoke(testCase);
          } catch (IllegalAccessException iae) {
            throw Exceptions.propagate(iae);
          } catch (InvocationTargetException ite) {
            throw Exceptions.propagate(ite);
          }
        }
      });
      listener.blockUntilResolved();
      notifier.fireTestFinished(desc);
    }
  }

  private class LazyTestListener implements LazyAssert.IListener {

    private final RunNotifier notifier;
    private final Description desc;
    private final Semaphore sema = new Semaphore(0);
    private final List<Pair<Throwable, Runnable>> onResolved = Factory.newArrayList();
    private int expected = 0;

    public LazyTestListener(RunNotifier notifier, Description desc) {
      this.notifier = notifier;
      this.desc = desc;
    }

    @Override
    public void waitForPromise(Promise<?> that) {
      expected++;
      that.onResolved(new ICallback<Object>() {
        @Override
        public void onSuccess(Object value) {
          sema.release();
        }
        @Override
        public void onFailure(Throwable error) {
          sema.release();
        }
      });
    }

    @Override
    public void whenResolved(Throwable location, Runnable callback) {
      onResolved.add(Pair.of(location, callback));
    }

    private void blockUntilResolved() {
      try {
        sema.acquire(expected);
        expected = 0;
      } catch (InterruptedException ie) {
        throw Exceptions.propagate(ie);
      }
      for (Pair<Throwable, Runnable> entry : this.onResolved) {
        try {
          entry.getSecond().run();
        } catch (Throwable re) {
          re.setStackTrace(entry.getFirst().getStackTrace());
          notifier.fireTestFailure(new Failure(desc, re));
        }
      }
    }

  }

}
