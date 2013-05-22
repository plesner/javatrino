package org.ne.utrino.compiler;

import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.value.RNull;

/**
 * A synthetic symbol representing some other symbol that was captured by a
 * lambda.s
 */
public class CapturedSymbol implements ISymbol {

  private final ISymbol outer;
  private final int index;

  public CapturedSymbol(ISymbol outer, int index) {
    this.outer = outer;
    this.index = index;
  }

  @Override
  public void read(Assembler assm) {
    assm.push(RNull.get());
  }

}
