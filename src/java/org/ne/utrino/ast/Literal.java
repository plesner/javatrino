package org.ne.utrino.ast;

import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.interpreter.Opcode;
import org.ne.utrino.util.Assert;
import org.ne.utrino.value.IValue;
/**
 * An expression that evaluates immediately to a literal object.
 */
public class Literal implements IExpression {

  private final IValue value;

  public Literal(IValue value) {
    this.value = Assert.notNull(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public void emit(Assembler assm) {
    int index = assm.registerConstant(value);
    assm.write(Opcode.PUSH, index);
  }

}
