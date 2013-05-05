package org.ne.utrino.ast;

import java.util.List;
/**
 * The full contents of a neutrino unit, typically a file.
 */
public class Unit {

  private final List<IDeclaration> decls;

  public Unit(List<IDeclaration> decls) {
    this.decls = decls;
  }

  @Override
  public String toString() {
    return "(unit " + decls + ")";
  }

  /**
   * Visits all declarations with the given visitor.
   */
  public void accept(IDeclaration.IVisitor visitor) {
    for (IDeclaration decl : decls)
      decl.accept(visitor);
  }

}
