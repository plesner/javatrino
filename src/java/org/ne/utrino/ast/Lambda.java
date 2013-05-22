package org.ne.utrino.ast;

import java.util.Map;

import org.ne.utrino.ast.MethodHeader.Parameter;
import org.ne.utrino.compiler.CapturedSymbol;
import org.ne.utrino.compiler.IScope;
import org.ne.utrino.compiler.ISymbol;
import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.interpreter.CodeBlock;
import org.ne.utrino.runtime.Compiler;
import org.ne.utrino.runtime.MethodSpace;
import org.ne.utrino.runtime.Signature;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Name;
import org.ne.utrino.value.Phase;
import org.ne.utrino.value.RLambda;
import org.ne.utrino.value.RMethod;

public class Lambda implements IExpression {

  private final MethodHeader header;
  private final IExpression body;
  private final Map<ISymbol, ISymbol> captures = Factory.newHashMap();

  public Lambda(MethodHeader header, IExpression body) {
    this.header = header;
    this.body = body;
  }

  @Override
  public <T> void accept(IVisitor<T> visitor, T data) {
    visitor.visitLambda(this, data);
  }

  @Override
  public void emit(Assembler assm) {
    Signature sig = header.toSignature();
    CodeBlock code = Compiler.compile(sig, body, assm.getContext());
    MethodSpace methodSpace = new MethodSpace();
    methodSpace.addMethod(new RMethod(sig, code));
    RLambda lambda = new RLambda(methodSpace);
    Assert.that(lambda.trySetPhase(Phase.SHALLOW_IMMUTABLE));
    assm.push(lambda);
  }

  /**
   * Returns the lambda body.
   */
  public IExpression getBody() {
    return this.body;
  }

  @Override
  public String toString() {
    return "(fn " + header + " => " + body + ")";
  }

  /**
   * Read the given value through this lambda with the given outer scope.
   */
  public ISymbol readName(Name name, IScope scope) {
    for (Parameter param : header.getParameters()) {
      if (param.getName().equals(name))
        return param.getSymbol();
    }
    ISymbol outer = scope.readName(name);
    ISymbol captured = captures.get(outer);
    if (captured == null) {
      captured = new CapturedSymbol(outer, captures.size());
      captures.put(outer, captured);
    }
    return captured;
  }

}
