package org.ne.utrino.interpreter;

import static org.ne.utrino.testing.TestFactory.toValue;

import java.util.List;

import org.junit.Test;
import org.ne.utrino.ast.IExpression;
import org.ne.utrino.runtime.Compiler;
import org.ne.utrino.runtime.NativeMethods;
import org.ne.utrino.syntax.Parser;
import org.ne.utrino.syntax.Token;
import org.ne.utrino.syntax.Tokenizer;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.Phase;
import org.ne.utrino.value.RContext;

import junit.framework.TestCase;

public class InterpreterTest extends TestCase {

  private static IValue run(IExpression expr, RContext context) {
    return Interpreter.interpret(Compiler.compileExpression(expr, context));
  }

  private static IValue run(String str) {
    List<Token> tokens = Tokenizer.tokenize(str);
    IExpression expr = new Parser(tokens).parseExpression();
    RContext context = new RContext();
    NativeMethods.addToContext(context);
    assertTrue(context.trySetPhase(Phase.SHALLOW_IMMUTABLE));
    return run(expr, context);
  }

  @Test
  public void testSimple() {
    assertEquals(toValue(8), run("8"));
    assertEquals(toValue(7), run("3 + 4"));
    assertEquals(toValue(12), run("3 + 4 + 5"));
    assertEquals(toValue(-1), run("3 - 4"));
    assertEquals(toValue(true), run("6 == 6"));
    assertEquals(toValue(false), run("6 == 7"));
    assertEquals(toValue(3), run("(fn => 3)()"));
    assertEquals(toValue(7), run("(fn => (3 + 4))()"));
  }

  @Test
  public void testLocal() {
    assertEquals(toValue(5), run("def $x := 4 in 5"));
    assertEquals(toValue(4), run("def $x := 4 in $x"));
    assertEquals(toValue(6), run("def $x := 6 in def $y := 7 in $x"));
    assertEquals(toValue(7), run("def $x := 6 in def $y := 7 in $y"));
  }

}
