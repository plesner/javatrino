package org.ne.utrino.compiler;

import org.ne.utrino.ast.LocalDeclaration;
import org.ne.utrino.interpreter.Assembler;

/**
 * A variable symbol.
 */
public class LocalSymbol implements ISymbol {

  private final LocalDeclaration origin;

  public LocalSymbol(LocalDeclaration origin) {
    this.origin = origin;
  }

  @Override
  public void read(Assembler assm) {
    int index = assm.getLocalIndex(this);
    assm.readLocal(index);
  }

}
