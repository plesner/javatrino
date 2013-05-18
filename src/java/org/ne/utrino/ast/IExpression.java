package org.ne.utrino.ast;

import org.ne.utrino.interpreter.Assembler;


/**
 * An abstract syntax tree expression.
 */
public interface IExpression {

  /**
   * Generate code for this type of expression using the given assembler.
   */
  public void emit(Assembler assm);

}
