package org.ne.utrino.compiler;

import org.ne.utrino.interpreter.Assembler;

/**
 * A source symbol.
 */
public interface ISymbol {

  /**
   * Emit code for reading this symbol on the given assembler.
   */
  public void read(Assembler assm);

}
