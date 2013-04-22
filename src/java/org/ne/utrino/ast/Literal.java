package org.ne.utrino.ast;

import org.ne.utrino.util.Assert;
import org.ne.utrino.value.IValue;

public class Literal implements IExpression {

  private final IValue value;

  public Literal(IValue value) {
    this.value = Assert.notNull(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
