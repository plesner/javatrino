package org.ne.utrino.ast;

public class NameDeclaration implements IDeclaration {

  private final ISymbol name;
  private final IExpression value;

  public NameDeclaration(ISymbol name, IExpression value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String toString() {
    return "(def " + name + " " + value + ")";
  }

}
