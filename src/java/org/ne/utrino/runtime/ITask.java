package org.ne.utrino.runtime;

import org.ne.utrino.value.IValue;

/**
 * A task to be executed in context of a process. A task must only affect one
 * process.
 */
public interface ITask<T extends IValue> {

  /**
   * Perform the task.
   */
  public T execute(Process process);

}
