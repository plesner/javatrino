package org.ne.utrino.ast;

/**
 * A toplevel side-effect-less declaration.
 */
public interface IDeclaration {

  /**
   * A visitor for declarations.
   */
  public interface IVisitor {

    public void visitNameDeclaration(NameDeclaration that);

  }

  /**
   * Invoke the given visitor on this declaration.
   */
  public void accept(IVisitor visitor);

}
