package org.ne.utrino.syntax;

import java.util.List;

import junit.framework.TestCase;

import org.ne.utrino.syntax.IToken.Type;
import org.ne.utrino.util.Factory;
import org.junit.Test;


public class TokenizerTest extends TestCase {

  private void runScanTest(String str, Token... tokens) {
    runScanTest(true, str, tokens);
  }

  private void runRawScanTest(String str, Token... tokens) {
    runScanTest(false, str, tokens);
  }

  private void runScanTest(boolean insertEther, String str, Token[] tokens) {
    List<Token> expected = Factory.newArrayList();
    boolean first = true;
    for (Token token : tokens) {
      if (insertEther) {
        if (first) first = false;
        else expected.add(sp(" "));
      }
      expected.add(token);
    }
    List<Token> found = Tokenizer.tokenize(str, Token.getFactory());
    assertEquals(expected, found);
  }

  @Test
  public void testScanning() {
    runScanTest("foo", wd("foo"));
    runScanTest("foo bar baz", wd("foo"), wd("bar"), wd("baz"));
    runScanTest("$foo", id("foo"));
    runRawScanTest("$foo$bar$baz", id("foo"), id("bar"), id("baz"));
    runRawScanTest("foo$bar baz", wd("foo"), id("bar"), sp(" "), wd("baz"));
    runScanTest(".foo", op("foo"));
    runRawScanTest(".foo.bar.baz", op("foo"), op("bar"), op("baz"));
    runRawScanTest("$foo.bar", id("foo"), op("bar"));
    runScanTest("+ ++ +++", op("+"), op("++"), op("+++"));
    runScanTest("+ - * / % < > =", op("+"), op("-"), op("*"), op("/"), op("%"),
        op("<"), op(">"), op("="));
    runScanTest("$foo + $bar", id("foo"), op("+"), id("bar"));
    runRawScanTest("(){};", pt(Type.LPAREN), pt(Type.RPAREN), pt(Type.LBRACE),
        pt(Type.RBRACE), pt(Type.SEMI));
    runRawScanTest("foo:", kw("foo"));
    runScanTest("10", nm("10"));
    runScanTest("10 000", nm("10"), nm("000"));
    runScanTest("\"boo\"", st("boo"));
    runScanTest("def $x := 4 ;", wd("def"), id("x"), op(":="), nm("4"),
        pt(Type.SEMI));
    runScanTest("for $i in 0 -> 10", wd("for"), id("i"), wd("in"), nm("0"),
        op("->"), nm("10"));
    runScanTest("+ - * /", op("+"), op("-"), op("*"), op("/"));
    runScanTest("( ) { } ; # @", pt(Type.LPAREN), pt(Type.RPAREN),
        pt(Type.LBRACE), pt(Type.RBRACE), pt(Type.SEMI), pt(Type.HASH),
        pt(Type.AT));
    runRawScanTest("## foo", ct("## foo"));
    runRawScanTest("## foo\n## bar", ct("## foo"), nl('\n'), ct("## bar"));
    runRawScanTest("## foo\n## bar", ct("## foo"), nl('\n'), ct("## bar"));
    runRawScanTest(" \n\n ", sp(" "), nl('\n'), nl('\n'), sp(" "));
    runRawScanTest("#{ foo }#", ct("#{ foo }#"));
    runRawScanTest("#{ \n }#", ct("#{ \n }#"));
    runRawScanTest("#{ \n\n }#", ct("#{ \n\n }#"));
  }
  
  private Token pt(Type type) {
    return Token.getFactory().newPunctuation(type);
  }
  
  private Token st(String string) {
    return Token.getFactory().newString(string);
  }
  
  private Token kw(String string) {
    return Token.getFactory().newKeyword(string);
  }

  private Token nm(String string) {
    return Token.getFactory().newNumber(string);
  }

  private Token op(String string) {
    return Token.getFactory().newOperator(string);
  }

  private Token id(String string) {
    return Token.getFactory().newIdentifier(string);
  }

  private Token wd(String string) {
    return Token.getFactory().newWord(string);
  }

  private static Token sp(String string) {
    return Token.getFactory().newSpace(string);
  }

  private static Token nl(char value) {
    return Token.getFactory().newNewline(value);
  }

  private static Token ct(String string) {
    return Token.getFactory().newComment(string);
  }

}
