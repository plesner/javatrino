package org.ne.utrino.runtime;

import org.ne.utrino.interpreter.Activation;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RInternalData;

/**
 * A native method is one that has special support in the language implementation.
 * For efficiency native methods have the option to rewrite themselves into
 * raw bytecodes.
 */
public abstract class RNativeMethod extends RInternalData {

  /**
   * Information about whether and how to inline a native method.
   */
  public static class InlineInfo {

    public boolean canInline = false;

  }

  /**
   * Perform this native method with arguments given through the given activation.
   * As a side-effect, if this method can be inlined into the raw bytecode of
   * the caller, that information can be set on the inline info object.
   */
  public abstract IValue invoke(Activation activation, InlineInfo inlineInfo);

}
