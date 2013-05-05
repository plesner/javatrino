package org.ne.utrino.runtime;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
/**
 * A runtime, the home of all the processes.
 */
public class Runtime {

  private final List<Thread> workers = Factory.newArrayList();
  private final LinkedBlockingQueue<Process> processes = new LinkedBlockingQueue<Process>();
  private volatile boolean keepRunning = true;

  /**
   * Creates a new runtime that executes using the given number of threads.
   */
  public Runtime(int threadCount) {
    Assert.that(threadCount >= 1);
    for (int i = 0; i < threadCount; i++) {
      final int workerIndex = i;
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
          runWorker(workerIndex);
        }
      });
      workers.add(thread);
    }
  }

  /**
   * Starts the worker threads running.
   */
  public void start() {
    for (Thread thread : workers)
      thread.start();
  }

  /**
   * Doesn't wait for the workers to finish but just stops them forcefully.
   */
  public void stop() {
    this.keepRunning = false;
    for (Thread thread : this.workers)
      thread.interrupt();
    for (Thread thread : this.workers) {
      try {
        thread.join();
      } catch (InterruptedException ie) {
        return;
      }
    }
    this.workers.clear();
  }

  /**
   * Creates a new process object controlled by this runtime.
   */
  public Process newProcess() {
    return new Process(this);
  }

  /**
   * All the worker threads will be executing this method.
   */
  protected void runWorker(int workerIndex) {
    while (this.keepRunning) {
      Process next;
      try {
        next = processes.take();
      } catch (InterruptedException ie) {
        return;
      }
      synchronized (this) {
        next.clearScheduled();
      }
      next.step();
    }
  }

  public synchronized void ensureScheduled(Process process) {
    if (!process.getAndSetScheduled())
      processes.offer(process);
  }

}
