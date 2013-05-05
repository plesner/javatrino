package org.ne.utrino.syntax;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.ne.utrino.ast.Dynamic;
import org.ne.utrino.ast.IDeclaration;
import org.ne.utrino.ast.IExpression;
import org.ne.utrino.ast.ISymbol;
import org.ne.utrino.ast.Literal;
import org.ne.utrino.ast.NameDeclaration;
import org.ne.utrino.ast.Static;
import org.ne.utrino.ast.Unit;
import org.ne.utrino.util.Name;
import org.ne.utrino.value.RInteger;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

  private void checkSymbol(String str, ISymbol expected) {
    List<Token> tokens = Tokenizer.tokenize(str);
    ISymbol symbol = new Parser(tokens).parseSymbol();
    assertEquals(expected, symbol);
  }

  @Test
  public void testSymbols() {
    checkSymbol("$foo:bar:baz", dyn("foo", "bar", "baz"));
    checkSymbol("$foo:bar", dyn("foo", "bar"));
    checkSymbol("$foo", dyn("foo"));
    checkSymbol("@foo:bar:baz", stc("foo", "bar", "baz"));
  }

  private void checkUnit(String str, IDeclaration... expectedDecls) {
    List<Token> tokens = Tokenizer.tokenize(str);
    Unit result = Parser.parseUnit(tokens);
    Unit expected = new Unit(Arrays.asList(expectedDecls));
    assertEquals(expected.toString(), result.toString());
  }

  @Test
  public void testDeclaration() {
    checkUnit("def $x := 4;", ndc(dyn("x"), lit(4)));
    checkUnit("def $x:y:z := 4;", ndc(dyn("x", "y", "z"), lit(4)));
    checkUnit("def @x := 4;", ndc(stc("x"), lit(4)));
    checkUnit("def $x := 4; def $y := 5;",
        ndc(dyn("x"), lit(4)),
        ndc(dyn("y"), lit(5)));
  }

  private NameDeclaration ndc(ISymbol name, IExpression value) {
    return new NameDeclaration(name, value);
  }

  private Dynamic dyn(String... parts) {
    return new Dynamic(Name.of(parts));
  }

  private Static stc(String... parts) {
    return new Static(Name.of(parts));
  }

  private Literal lit(int value) {
    return new Literal(new RInteger(value));
  }

}
