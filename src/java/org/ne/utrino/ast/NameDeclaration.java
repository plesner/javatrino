package org.ne.utrino.ast;

import org.ne.utrino.util.Name;

public class NameDeclaration implements IDeclaration {

  private final Name name;
  private final IExpression value;

  public NameDeclaration(Name name, IExpression value) {
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
  public Name getName() {
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
