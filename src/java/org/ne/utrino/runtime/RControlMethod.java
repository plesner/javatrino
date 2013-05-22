package org.ne.utrino.runtime;

import org.ne.utrino.interpreter.Activation;
import org.ne.utrino.interpreter.Interpreter;
import org.ne.utrino.value.RInternalData;
/**
 * A native method that is allowed to affect control flow.
 */
public abstract class RControlMethod extends RInternalData {

  /**
   * Perform this native method with arguments given through the given frame.
   * The method is allowed to affect control flow through the given interpreter
   * object.
   */
  public abstract void invoke(Activation frame, Interpreter inter);

}
