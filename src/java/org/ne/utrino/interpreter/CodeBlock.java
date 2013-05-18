package org.ne.utrino.interpreter;

import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RContext;

/**
 * Executable code block.
 */
public class CodeBlock {

  private final RContext context;
  private final int[] code;
  private final IValue[] constants;

  public CodeBlock(RContext context, int[] code, IValue[] constants) {
    this.context = context;
    this.code = code;
    this.constants = constants;
  }

  /**
   * Returns the bytecode array.
   */
  public int[] getCode() {
    return this.code;
  }

  /**
   * Returns the constant pool.
   */
  public IValue[] getConstants() {
    return this.constants;
  }

  /**
   * Returns the context in which this code runs.
   */
  public RContext getContext() {
    return this.context;
  }

}
