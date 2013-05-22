package org.ne.utrino.ast;

import org.ne.utrino.compiler.ISymbol;
import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Name;

public class Identifier implements IExpression {

  private final Name name;
  private ISymbol symbol;

  public Identifier(Name name) {
    this.name = name;
  }

  /**
   * Resolves the symbol read by this identifier.
   */
  public void resolve(ISymbol value) {
    Assert.equals(null, symbol);
    this.symbol = Assert.notNull(value);
  }

  /**
   * Returns the name of the variable being read by this identifier.
   */
  public Name getName() {
    return this.name;
  }

  @Override
  public <T> void accept(IVisitor<T> visitor, T data) {
    visitor.visitIdentifier(this, data);
  }

  @Override
  public void emit(Assembler assm) {
    Assert.notNull(this.symbol);
    this.symbol.read(assm);
  }

  @Override
  public String toString() {
    return "$" + name;
  }

}
