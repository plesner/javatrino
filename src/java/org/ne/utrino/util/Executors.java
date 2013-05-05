package org.ne.utrino.util;

import java.util.concurrent.Executor;

/**
 * Factory for various kinds of executors.
 */
public class Executors {

  /**
   * Singleton same thread executor.
   */
  private static final Executor SAME_THREAD_EXECUTOR = new Executor() {
    @Override
    public void execute(Runnable command) {
      command.run();
    }
  };

  /**
   * Returns an executor that executes tasks immediately on the calling thread.
   */
  public static Executor sameThread() {
    return SAME_THREAD_EXECUTOR;
  }

}
