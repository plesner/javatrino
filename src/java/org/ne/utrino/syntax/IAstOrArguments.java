package org.ne.utrino.syntax;

import java.util.List;

public interface IAstOrArguments {

  /**
   * If this ast is used as arguments to an invocation, which arguments
   * does it represent?
   */
  public List<Ast> asArguments();

  /**
   * If this ast is used as a plain expression, which expression does
   * it represent?
   */
  public Ast asAst();

}
