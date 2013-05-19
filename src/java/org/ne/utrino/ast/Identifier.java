package org.ne.utrino.ast;

import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.util.Name;

public class Identifier implements IExpression {

  private final Name name;

  public Identifier(Name name) {
    this.name = name;
  }

  @Override
  public void emit(Assembler assm) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return "$" + name;
  }

}
