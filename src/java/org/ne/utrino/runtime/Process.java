package org.ne.utrino.runtime;

import java.util.LinkedList;

import org.ne.utrino.util.Factory;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RPromise;
/**
 * A separate thread of control with an event queue.
 */
public class Process {

  /**
   * The state associated with a task waiting to be executed.
   */
  private static class PendingTask<T extends IValue> {

    private final ITask<T> task;
    private final RPromise<T> promise;

    public PendingTask(ITask<T> task) {
      this.task = task;
      this.promise = RPromise.empty();
    }

    public void execute(Process process) {
      this.promise.fulfill(task.execute(process));
    }

  }

  private boolean isScheduled = false;
  private final Runtime runtime;
  private final LinkedList<PendingTask<?>> work = Factory.newLinkedList();

  public Process(Runtime runtime) {
    this.runtime = runtime;
  }

  public synchronized <T extends IValue> RPromise<T> schedule(ITask<T> task) {
    PendingTask<T> pending = new PendingTask<T>(task);
    this.work.add(pending);
    this.runtime.ensureScheduled(this);
    return pending.promise;
  }

  public void step() {
    PendingTask<?> first;
    synchronized (this) {
      first = work.removeFirst();
    }
    first.execute(this);
    if (!work.isEmpty())
      this.runtime.ensureScheduled(this);
  }

  public synchronized boolean getAndSetScheduled() {
    boolean result = this.isScheduled;
    this.isScheduled = true;
    return result;
  }

  public synchronized void clearScheduled() {
    this.isScheduled = false;
  }

}
