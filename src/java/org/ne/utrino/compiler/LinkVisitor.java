package org.ne.utrino.compiler;

import org.ne.utrino.ast.IExpression;
import org.ne.utrino.ast.IExpression.IVisitor;
import org.ne.utrino.ast.Identifier;
import org.ne.utrino.ast.Invocation;
import org.ne.utrino.ast.Lambda;
import org.ne.utrino.ast.Literal;
import org.ne.utrino.ast.LocalDeclaration;
import org.ne.utrino.util.Name;

public class LinkVisitor implements IVisitor<IScope> {

  @Override
  public void visitIdentifier(Identifier that, IScope scope) {
    ISymbol symbol = scope.readName(that.getName());
    that.resolve(symbol);
  }

  @Override
  public void visitLambda(final Lambda that, final IScope scope) {
    IScope inner = new IScope() {
      @Override
      public ISymbol readName(Name name) {
        return that.readName(name, scope);
      }
    };
    that.getBody().accept(this, inner);
  }

  @Override
  public void visitLiteral(Literal that, IScope data) {
    // ignore
  }

  @Override
  public void visitLocalDeclaration(final LocalDeclaration that, final IScope outer) {
    IScope inner = new IScope() {
      @Override
      public ISymbol readName(Name name) {
        return name.equals(that.getName()) ? that.getSymbol() : outer.readName(name);
      }
    };
    that.getValue().accept(this, outer);
    that.getBody().accept(this, inner);
  }

  @Override
  public void visitInvocation(Invocation that, IScope scope) {
    for (IExpression arg : that.getArguments())
      arg.accept(this, scope);
  }

}
