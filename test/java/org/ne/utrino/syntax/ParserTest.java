package org.ne.utrino.syntax;

import static org.ne.utrino.testing.TestFactory.toValue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.ne.utrino.ast.Dynamic;
import org.ne.utrino.ast.IDeclaration;
import org.ne.utrino.ast.IExpression;
import org.ne.utrino.ast.ISymbol;
import org.ne.utrino.ast.Identifier;
import org.ne.utrino.ast.Invocation;
import org.ne.utrino.ast.Lambda;
import org.ne.utrino.ast.Literal;
import org.ne.utrino.ast.MethodHeader;
import org.ne.utrino.ast.MethodHeader.Parameter;
import org.ne.utrino.ast.NameDeclaration;
import org.ne.utrino.ast.Static;
import org.ne.utrino.ast.Unit;
import org.ne.utrino.util.Name;
import org.ne.utrino.util.Pair;
import org.ne.utrino.value.ITagValue;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

  private void checkSymbol(String str, ISymbol expected) {
    List<Token> tokens = Tokenizer.tokenize(str);
    ISymbol symbol = new Parser(tokens).parseSymbol();
    assertEquals(expected, symbol);
  }

  @Test
  public void testSymbols() {
    checkSymbol("$foo:bar:baz", dn("foo", "bar", "baz"));
    checkSymbol("$foo:bar", dn("foo", "bar"));
    checkSymbol("$foo", dn("foo"));
    checkSymbol("@foo:bar:baz", st("foo", "bar", "baz"));
  }

  private void checkUnit(String str, IDeclaration... expectedDecls) {
    List<Token> tokens = Tokenizer.tokenize(str);
    Unit result = Parser.parseUnit(tokens);
    Unit expected = new Unit(Arrays.asList(expectedDecls));
    assertEquals(expected.toString(), result.toString());
  }

  @Test
  public void testDeclaration() {
    checkUnit("def $x := 4;", nd(dn("x"), lt(4)));
    checkUnit("def $x:y:z := 4;", nd(dn("x", "y", "z"), lt(4)));
    checkUnit("def @x := 4;", nd(st("x"), lt(4)));
    checkUnit("def $x := 4; def $y := 5;",
        nd(dn("x"), lt(4)),
        nd(dn("y"), lt(5)));
  }

  private void checkExpression(String str, IExpression expected) {
    List<Token> tokens = Tokenizer.tokenize(str);
    IExpression result = new Parser(tokens).parseExpression();
    assertEquals(expected.toString(), result.toString());
  }

  @Test
  public void testExpression() {
    checkExpression("1", lt(1));
    checkExpression("2 + 3", bn(lt(2), "+", lt(3)));
    checkExpression("2 + (3)", bn(lt(2), "+", lt(3)));
    checkExpression("2 + 3 + 4", bn(bn(lt(2), "+", lt(3)), "+", lt(4)));
    checkExpression("$foo + 3", bn(id("foo"), "+", lt(3)));
    checkExpression("$foo.plus 3", bn(id("foo"), "plus", lt(3)));
    checkExpression("$foo.plus(3)", bn(id("foo"), "plus", lt(3)));
    checkExpression("$foo.plus(3, 4)", bn(id("foo"), "plus", lt(3), lt(4)));
    checkExpression("$foo()", bn(id("foo"), "()"));
    checkExpression("$foo(5)", bn(id("foo"), "()", lt(5)));
    checkExpression("$foo(6, 7)", bn(id("foo"), "()", lt(6), lt(7)));
    checkExpression("$foo[]", bn(id("foo"), "[]"));
    checkExpression("$foo[5]", bn(id("foo"), "[]", lt(5)));
    checkExpression("$foo[6, 7]", bn(id("foo"), "[]", lt(6), lt(7)));
    checkExpression("$foo() + $bar()", bn(bn(id("foo"), "()"), "+", bn(id("bar"), "()")));
    checkExpression("fn => 4", lm(hd("()"), lt(4)));
    checkExpression("fn () => 5", lm(hd("()"), lt(5)));
    checkExpression("fn ($a) => 6", lm(hd("()", pm("a")), lt(6)));
    checkExpression("fn ($a, $b) => 7", lm(hd("()", pm("a"), pm("b")), lt(7)));
    checkExpression("fn ($a, $b, $c) => 8", lm(hd("()", pm("a"), pm("b"), pm("c")), lt(8)));
  }

  /**
   * Creates and returns a new identifier.
   */
  private static Identifier id(String... parts) {
    return new Identifier(Name.of(parts));
  }

  /**
   * Creates a new parameter.
   */
  private static Parameter pm(String... parts) {
    return new Parameter(Name.of(parts));
  }

  /**
   * Creates a new method header.
   */
  private static MethodHeader hd(String name, Parameter... params) {
    return new MethodHeader(name, Arrays.asList(params));
  }

  /**
   * Creates a new lambda.
   */
  private static Lambda lm(MethodHeader header, IExpression body) {
    return new Lambda(header, body);
  }

  /**
   * Creates a new name declaration.
   */
  private static NameDeclaration nd(ISymbol name, IExpression value) {
    return new NameDeclaration(name, value);
  }

  /**
   * Creates a new dynamic name.
   */
  private static Dynamic dn(String... parts) {
    return new Dynamic(Name.of(parts));
  }

  /**
   * Creates a new static name.
   */
  private static Static st(String... parts) {
    return new Static(Name.of(parts));
  }

  /**
   * Creates a literal.
   */
  private static Literal lt(Object obj) {
    return new Literal(toValue(obj));
  }

  /**
   * Creates a new invocation argument.
   */
  private static Pair<ITagValue, IExpression> ag(ITagValue tag, IExpression value) {
    return Pair.of(tag, value);
  }

  /**
   * Creates a general invocation.
   */
  @SafeVarargs
  private static Invocation iv(Pair<ITagValue, IExpression>... args) {
    return new Invocation(Arrays.asList(args));
  }

  /**
   * Creates a binary invocation.
   */
  private static Invocation bn(IExpression left, String op, IExpression... right) {
    Invocation.Builder builder = new Invocation.Builder();
    builder.setThis(left);
    builder.setName(op);
    for (int i = 0; i < right.length; i++)
      builder.setPositional(i, right[i]);
    return builder.build();
  }

}
