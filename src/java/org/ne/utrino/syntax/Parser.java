package org.ne.utrino.syntax;

import java.util.Collections;
import java.util.List;

import org.ne.utrino.ast.IDeclaration;
import org.ne.utrino.ast.IExpression;
import org.ne.utrino.ast.Identifier;
import org.ne.utrino.ast.Invocation;
import org.ne.utrino.ast.Lambda;
import org.ne.utrino.ast.Literal;
import org.ne.utrino.ast.LocalDeclaration;
import org.ne.utrino.ast.MethodHeader;
import org.ne.utrino.ast.MethodHeader.Parameter;
import org.ne.utrino.ast.NameDeclaration;
import org.ne.utrino.ast.Unit;
import org.ne.utrino.syntax.Token.DelimiterStatus;
import org.ne.utrino.syntax.Token.Type;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Name;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.RInteger;

public class Parser {

  private static final String DEF_WORD = "def";
  private static final String FN_WORD = "fn";
  private static final String IN_WORD = "in";
  private static final String ASSIGN_OP = ":=";
  private static final String TO_OP = "=>";

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
      IDeclaration decl = parseToplevelDeclaration();
      decls.add(decl);
    }
    return new Unit(decls);
  }

  private boolean atWord(String value) {
    return at(Type.WORD) && getCurrent().getValue().equals(value);
  }

  private boolean atOperator(String value) {
    return at(Type.OPERATOR) && getCurrent().getValue().equals(value);
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

  private Name expectIdentifier() {
    if (!at(Type.IDENTIFIER))
      throw newSyntaxError();
    Name value = getCurrent().getName();
    advance();
    return value;
  }


  private void expect(Type type) {
    if (!at(type))
      throw newSyntaxError();
    advance();
  }

  private SyntaxError newSyntaxError() {
    return new SyntaxError(getCurrent());
  }

  public Name parseName() {
    if (at(Type.IDENTIFIER)) {
      Token current = getCurrent();
      advance();
      return current.getName();
    } else {
      throw newSyntaxError();
    }
  }

  public NameDeclaration parseToplevelDeclaration() {
    NameDeclaration result = parseNameDeclaration();
    if (hasMore()) {
      DelimiterStatus status = getCurrent().getDelimiterStatus();
      if (!status.isDelimiter())
        throw newSyntaxError();
      if (status.isExplicit())
        advance();
    }
    return result;
  }

  private NameDeclaration parseNameDeclaration() {
    expectWord(DEF_WORD);
    Name name = parseName();
    IExpression value = parseDeclarationTail();
    return new NameDeclaration(name, value);
  }

  private IExpression parseDeclarationTail() {
    expectOperator(ASSIGN_OP);
    return parseExpression();
  }

  public IExpression parseOperatorExpression() {
    IExpression left = parseApplicationExpression();
    while (hasMore() && at(Type.OPERATOR)) {
      Invocation.Builder builder = new Invocation.Builder();
      builder.setThis(left);
      builder.setName(expectOperator());
      addArguments(builder, parseArguments(Type.LPAREN, Type.RPAREN, true));
      left = builder.build();
    }
    return left;
  }

  /**
   * Are we at the beginning of an application expression?
   */
  private boolean atApplication() {
    return at(Type.LPAREN) || at(Type.LBRACK);
  }

  /**
   * Parses an application (function call or indexing).
   */
  public IExpression parseApplicationExpression() {
    IExpression left = parseAtomicExpression();
    while (hasMore() && atApplication()) {
      Invocation.Builder builder = new Invocation.Builder();
      builder.setThis(left);
      if (at(Type.LPAREN)) {
        builder.setName("()");
        addArguments(builder, parseArguments(Type.LPAREN, Type.RPAREN, false));
      } else {
        builder.setName("[]");
        addArguments(builder, parseArguments(Type.LBRACK, Type.RBRACK, false));
      }
      left = builder.build();
    }
    return left;
  }

  /**
   * Adds the given set of arguments to this list of invocation entries.
   */
  private void addArguments(Invocation.Builder builder, List<IExpression> args) {
    for (int i = 0; i < args.size(); i++)
      builder.setPositional(i, args.get(i));
  }

  public IExpression parseExpression() {
    if (atWord(FN_WORD)) {
      expectWord(FN_WORD);
      MethodHeader header = parseMethodHeader();
      expectOperator(TO_OP);
      IExpression body = parseExpression();
      return new Lambda(header, body);
    } else if (atWord(DEF_WORD)) {
      NameDeclaration decl = parseNameDeclaration();
      expectWord(IN_WORD);
      IExpression value = parseExpression();
      return new LocalDeclaration(decl, value);
    } else {
      return parseOperatorExpression();
    }
  }

  private MethodHeader parseMethodHeader() {
    if (at(Type.LPAREN)) {
      expect(Type.LPAREN);
      List<Parameter> params = parseParameters(Type.RPAREN);
      expect(Type.RPAREN);
      return new MethodHeader("()", params);
    } else if (atOperator("=>")) {
      return new MethodHeader("()", Collections.<MethodHeader.Parameter>emptyList());
    } else {
      throw newSyntaxError();
    }
  }

  /**
   * Parses a whole parameter list.
   */
  private List<Parameter> parseParameters(Type end) {
    if (at(end))
      return Collections.emptyList();
    List<Parameter> params = Factory.newArrayList();
    int index = 0;
    params.add(parseParameter(RInteger.of(index++)));
    while (hasMore() && at(Type.COMMA)) {
      expect(Type.COMMA);
      params.add(parseParameter(RInteger.of(index++)));
    }
    return params;
  }

  /**
   * Parses a single possibly tagged parameter.
   */
  private Parameter parseParameter(ITagValue implicitTag) {
    Name name = expectIdentifier();
    return new Parameter(name, implicitTag);
  }

  /**
   * Parses a list of operation arguments.
   */
  private List<IExpression> parseArguments(Type start, Type end, boolean application) {
    if (at(start)) {
      expect(start);
      if (at(end)) {
        expect(end);
        return Collections.emptyList();
      }
      List<IExpression> exprs = Factory.newArrayList();
      exprs.add(parseExpression());
      while (hasMore() && at(Type.COMMA)) {
        expect(Type.COMMA);
        exprs.add(parseExpression());
      }
      expect(end);
      return exprs;
    } else {
      IExpression rest = application ? parseApplicationExpression() : parseAtomicExpression();
      return Collections.singletonList(rest);
    }
  }

  private IExpression parseAtomicExpression() {
    switch (getCurrent().getType()) {
    case NUMBER: {
      int value = Integer.parseInt(getCurrent().getValue());
      advance();
      return new Literal(new RInteger(value));
    }
    case LPAREN: {
      expect(Type.LPAREN);
      IExpression value = parseExpression();
      expect(Type.RPAREN);
      return value;
    }
    case IDENTIFIER: {
      Token current = getCurrent();
      advance();
      Name name = current.getName();
      return new Identifier(name);
    }
    default:
      throw newSyntaxError();
    }
  }

  public static Unit parseUnit(List<Token> tokens) {
    return new Parser(tokens).parseUnit();
  }

}
