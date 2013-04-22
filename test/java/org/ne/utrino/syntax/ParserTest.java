package org.ne.utrino.syntax;

import java.util.List;

import org.junit.Test;
import org.ne.utrino.ast.Dynamic;
import org.ne.utrino.ast.IDeclaration;
import org.ne.utrino.ast.IExpression;
import org.ne.utrino.ast.ISymbol;
import org.ne.utrino.ast.Literal;
import org.ne.utrino.ast.NameDeclaration;
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
  }

  private void checkDeclaration(String str, IDeclaration expected) {
    List<Token> tokens = Tokenizer.tokenize(str);
    IDeclaration decl = new Parser(tokens).parseDeclaration();
    assertEquals(expected.toString(), decl.toString());
  }

  @Test
  public void testDeclaration() {
    checkDeclaration("def $x := 4;", ndc(dyn("x"), lit(4)));
  }

  private NameDeclaration ndc(ISymbol name, IExpression value) {
    return new NameDeclaration(name, value);
  }

  private Dynamic dyn(String... parts) {
    return new Dynamic(Name.of(parts));
  }

  private Literal lit(int value) {
    return new Literal(new RInteger(value));
  }

}
