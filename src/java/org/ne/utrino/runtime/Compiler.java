package org.ne.utrino.runtime;

import java.util.List;

import org.ne.utrino.ast.IExpression;
import org.ne.utrino.ast.Unit;
import org.ne.utrino.compiler.LinkVisitor;
import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.interpreter.CodeBlock;
import org.ne.utrino.syntax.Parser;
import org.ne.utrino.syntax.Token;
import org.ne.utrino.syntax.Tokenizer;
import org.ne.utrino.value.RContext;

public class Compiler {

  public static void run(String code) {
    Runtime runtime = new Runtime(4);
    runtime.start();
    List<Token> tokens = Tokenizer.tokenize(code);
    Unit unit = Parser.parseUnit(tokens);
    compileUnit(runtime.newProcess(), unit);
  }

  private static void compileUnit(Process process, Unit unit) {

  }

  public static CodeBlock linkAndCompile(Signature signature, IExpression expr, RContext context) {
    expr.accept(new LinkVisitor(), null);
    return compile(signature, expr, context);
  }

  public static CodeBlock compile(Signature signature, IExpression expr, RContext context) {
    Assembler assm = new Assembler(signature, context);
    expr.emit(assm);
    assm.close();
    return assm.toCodeBlock();
  }

}
