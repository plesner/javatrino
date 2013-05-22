package org.ne.utrino.ast;

import org.ne.utrino.compiler.LocalSymbol;
import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.util.Name;

public class LocalDeclaration implements IExpression {

  private final NameDeclaration name;
  private final IExpression body;
  private final LocalSymbol symbol;

  public LocalDeclaration(NameDeclaration name, IExpression body) {
    this.name = name;
    this.body = body;
    this.symbol = new LocalSymbol(this);
  }

  @Override
  public <T> void accept(IVisitor<T> visitor, T data) {
    visitor.visitLocalDeclaration(this, data);
  }

  /**
   * Returns the symbol object that identifies this declaration.
   */
  public LocalSymbol getSymbol() {
    return symbol;
  }

  public Name getName() {
    return name.getName();
  }

  public IExpression getValue() {
    return name.getValue();
  }

  public IExpression getBody() {
    return body;
  }

  @Override
  public void emit(Assembler assm) {
    this.getValue().emit(assm);
    try {
      assm.setTopValueName(this.getSymbol());
      this.getBody().emit(assm);
    } finally {
      assm.popBelow();
      assm.forgetName(this.getSymbol());
    }
  }

  @Override
  public String toString() {
    return "(def " + name + " in " + body + ")";
  }

}
