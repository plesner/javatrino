package org.ne.utrino.syntax;

import java.util.List;

import org.junit.Test;
import org.ne.utrino.syntax.Token.DelimiterStatus;
import org.ne.utrino.syntax.Token.Type;
import org.ne.utrino.util.Factory;

import junit.framework.TestCase;


public class TokenizerTest extends TestCase {

  private void runScanTest(String str, Token... tokens) {
    List<Token> expected = Factory.newArrayList();
    for (Token token : tokens)
      expected.add(token);
    List<Token> found = Tokenizer.tokenize(str);
    assertEquals(expected, found);
  }

  @Test
  public void testSimpleTokens() {
    runScanTest("foo", wd("foo"));
    runScanTest("foo bar baz", wd("foo"), wd("bar"), wd("baz"));
    runScanTest("$foo", id("$foo"));
    runScanTest("$foo$bar$baz", id("$foo"), id("$bar"), id("$baz"));
    runScanTest("foo$bar baz", wd("foo"), id("$bar"), wd("baz"));
    runScanTest(".foo", op("foo"));
    runScanTest(".foo.bar.baz", op("foo"), op("bar"), op("baz"));
    runScanTest("$foo.bar", id("$foo"), op("bar"));
    runScanTest("+ ++ +++", op("+"), op("++"), op("+++"));
    runScanTest("+ - * / % < > =", op("+"), op("-"), op("*"), op("/"), op("%"),
        op("<"), op(">"), op("="));
    runScanTest("$foo + $bar", id("$foo"), op("+"), id("$bar"));
    runScanTest("$foo:bar:baz", id("$foo:bar:baz"));
    runScanTest("(){};", pt(Type.LPAREN), pt(Type.RPAREN), pt(Type.LBRACE),
        pt(Type.RBRACE), pt(Type.SEMI));
    runScanTest("foo:", kw("foo"));
    runScanTest("10", nm("10"));
    runScanTest("10 000", nm("10"), nm("000"));
    runScanTest("\"boo\"", st("boo"));
    runScanTest("def $x := 4 ;", wd("def"), id("$x"), op(":="), nm("4"),
        pt(Type.SEMI));
    runScanTest("for $i in 0 -> 10", wd("for"), id("$i"), wd("in"), nm("0"),
        op("->"), nm("10"));
    runScanTest("+ - * /", op("+"), op("-"), op("*"), op("/"));
  }

  @Test
  public void testImplicitSemis() {
    runScanTest("} foo bar", pt(Type.RBRACE), iwd("foo"), wd("bar"));
    runScanTest("} ; bar", pt(Type.RBRACE), pt(Type.SEMI), wd("bar"));
  }

  private Token pt(Type type) {
    return Token.newPunctuation(type, (type == Type.SEMI)
        ? DelimiterStatus.EXPLICIT
        : DelimiterStatus.NONE);
  }

  private Token st(String string) {
    return new Token(Type.STRING, string, DelimiterStatus.NONE);
  }

  private Token kw(String string) {
    return new Token(Type.TAG, string, DelimiterStatus.NONE);
  }

  private Token nm(String string) {
    return new Token(Type.NUMBER, string, DelimiterStatus.NONE);
  }

  private Token op(String string) {
    return new Token(Type.OPERATOR, string, DelimiterStatus.NONE);
  }

  private Token id(String string) {
    return new Token(Type.IDENTIFIER, string, DelimiterStatus.NONE);
  }

  private Token wd(String string) {
    return new Token(Type.WORD, string, DelimiterStatus.NONE);
  }

  private Token iwd(String string) {
    return new Token(Type.WORD, string, DelimiterStatus.IMPLICIT);
  }

}
