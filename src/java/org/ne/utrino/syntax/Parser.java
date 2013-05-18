package org.ne.utrino.syntax;

import java.util.List;

import org.ne.utrino.ast.Dynamic;
import org.ne.utrino.ast.IDeclaration;
import org.ne.utrino.ast.IExpression;
import org.ne.utrino.ast.ISymbol;
import org.ne.utrino.ast.Invocation;
import org.ne.utrino.ast.Literal;
import org.ne.utrino.ast.NameDeclaration;
import org.ne.utrino.ast.Static;
import org.ne.utrino.ast.Unit;
import org.ne.utrino.syntax.Token.DelimiterStatus;
import org.ne.utrino.syntax.Token.Type;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Name;
import org.ne.utrino.util.Pair;
import org.ne.utrino.value.RInteger;
import org.ne.utrino.value.RKey;
import org.ne.utrino.value.RString;

public class Parser {

  private static final String DEF_WORD = "def";
  private static final String ASSIGN_OP = ":=";

  private final List<Token> tokens;
  private int cursor = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  private boolean hasMore() {
    return cursor < tokens.size();
  }

  private Token getCurrent() {
    return tokens.get(cursor);
  }

  private void advance() {
    cursor++;
  }

  private Unit parseUnit() {
    List<IDeclaration> decls = Factory.newArrayList();
    while (hasMore()) {
      IDeclaration decl = parseDeclaration();
      decls.add(decl);
    }
    return new Unit(decls);
  }

  private void expectWord(String value) {
    expect(Type.WORD, value);
  }

  private void expectOperator(String value) {
    expect(Type.OPERATOR, value);
  }

  private boolean at(Type type) {
    return getCurrent().is(type);
  }

  private void expect(Type type, String value) {
    if (!at(type) || !getCurrent().getValue().equals(value))
      throw newSyntaxError();
    advance();
  }

  private String expectOperator() {
    if (!at(Type.OPERATOR))
      throw newSyntaxError();
    String value = getCurrent().getValue();
    advance();
    return value;
  }

  private SyntaxError newSyntaxError() {
    return new SyntaxError(getCurrent());
  }

  public ISymbol parseSymbol() {
    if (at(Type.IDENTIFIER)) {
      Token current = getCurrent();
      advance();
      Name name = current.getName();
      if (current.isDynamic()) {
        return new Dynamic(name);
      } else {
        return new Static(name);
      }
    } else {
      throw newSyntaxError();
    }
  }

  public IDeclaration parseDeclaration() {
    expectWord(DEF_WORD);
    ISymbol name = parseSymbol();
    IExpression value = parseDeclarationTail();
    if (hasMore()) {
      DelimiterStatus status = getCurrent().getDelimiterStatus();
      if (!status.isDelimiter())
        throw newSyntaxError();
      if (status.isExplicit())
        advance();
    }
    return new NameDeclaration(name, value);
  }

  private IExpression parseDeclarationTail() {
    expectOperator(ASSIGN_OP);
    return parseExpression();
  }

  public IExpression parseOperatorExpression() {
    IExpression left = parseAtomicExpression();
    while (hasMore() && at(Type.OPERATOR)) {
      String op = expectOperator();
      IExpression right = parseAtomicExpression();
      left = new Invocation(
          Pair.of(RKey.THIS, left),
          Pair.of(RKey.NAME, new Literal(RString.of(op))),
          Pair.of(RInteger.of(0), right));
    }
    return left;
  }

  public IExpression parseExpression() {
    return parseOperatorExpression();
  }

  private IExpression parseAtomicExpression() {
    switch (getCurrent().getType()) {
    case NUMBER: {
      int value = Integer.parseInt(getCurrent().getValue());
      advance();
      return new Literal(new RInteger(value));
    }
    default:
      throw newSyntaxError();
    }
  }

  public static Unit parseUnit(List<Token> tokens) {
    return new Parser(tokens).parseUnit();
  }

}
