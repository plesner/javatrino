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

  /**
   * Visitor interface the specifies the possible types of expression.
   */
  public interface IVisitor<T> {

    public void visitIdentifier(Identifier that, T data);

    public void visitLambda(Lambda that, T data);

    public void visitLiteral(Literal that, T data);

    public void visitLocalDeclaration(LocalDeclaration that, T data);

    public void visitInvocation(Invocation that, T data);

  }

  /**
   * Visit this expression with the given visitor.
   */
  public <T> void accept(IVisitor<T> visitor, T data);

}
