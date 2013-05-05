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

  /**
   * Returns the name being defined by this declaration.
   */
  public ISymbol getName() {
    return this.name;
  }

  /**
   * Returns the value of this declaration.
   */
  public IExpression getValue() {
    return this.value;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visitNameDeclaration(this);
  }

}
