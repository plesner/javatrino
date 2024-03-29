package org.ne.utrino.syntax;

import java.util.List;

import org.ne.utrino.syntax.Token.DelimiterStatus;
import org.ne.utrino.syntax.Token.LiteralToken;
import org.ne.utrino.syntax.Token.TagToken;
import org.ne.utrino.syntax.Token.Type;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Internal;
import org.ne.utrino.util.Name;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.RInteger;
import org.ne.utrino.value.RString;

/**
 * Utility for chopping a string into tokens.
 */
public class Tokenizer {

  private final ICharStream source;
  private DelimiterStatus nextDelimStatus = DelimiterStatus.NONE;

  public Tokenizer(ICharStream source) {
    this.source = source;
    this.skipEther();
  }

  /**
   * Is there more input?
   */
  private boolean hasMore() {
    return source.hasMore();
  }

  /**
   * Returns the current character.
   */
  private char getCurrent() {
    return source.getCurrent();
  }

  /**
   * Returns the current input cursor.
   */
  private int getCursor() {
    return source.getCursor();
  }

  private void advance() {
    source.advance();
  }

  /**
   * Can this character occur first in a word?
   */
  private static boolean isWordStart(char c) {
    return Character.isLetter(c) || (c == '_');
  }

  /**
   * Can this character occur after the first character of a word?
   */
  private static boolean isWordPart(char c) {
    return isWordStart(c) || Character.isDigit(c);
  }

  /**
   * Can this character occur first in a number?
   */
  private static boolean isNumberStart(char c) {
    return Character.isDigit(c);
  }

  /**
   * Can this character occur after the first character of a number?
   */
  private static boolean isNumberPart(char c) {
    return isNumberStart(c);
  }

  /**
   * Returns true if the given operation can occur as part of an operator.
   */
  private static boolean isOperatorPart(char c) {
    switch (c) {
    case '.':
      return true;
    default:
      return isOperatorStart(c);
    }
  }

  /**
   * Returns true if the given operation can occur as part of an operator.
   */
  private static boolean isOperatorStart(char c) {
    switch (c) {
    case '+': case '-': case '*': case '/': case '<': case '>': case '=':
    case '%': case ':':
      return true;
    default:
      return false;
    }
  }

  @Internal
  public static boolean isSpace(char c) {
    return Character.isWhitespace(c);
  }

  /**
   * Is this a whitespace but not a newline?
   */
  public static boolean isSpaceNotNewline(char c) {
    return isSpace(c) && !isNewline(c);
  }

  /**
   * Does this character terminate end-of-line comments?
   */
  public static boolean isNewline(char c) {
    return (c == '\n') || (c == '\r') || (c == '\f');
  }

  private void skipEther() {
    while (hasMore() && isSpace(getCurrent()))
      advance();
  }

  /**
   * Advances over the next token or tokens, adding them to the given
   * list.
   */
  public Token scanNext() {
    Assert.that(hasMore());
    Token result;
    DelimiterStatus delimStatus = this.nextDelimStatus;
    this.nextDelimStatus = DelimiterStatus.NONE;
    if (isWordStart(getCurrent())) {
      result = scanWord(delimStatus);
    } else if (isNumberStart(getCurrent())) {
      result = scanNumber(delimStatus);
    } else if (isOperatorStart(getCurrent())) {
      result = scanOperator(delimStatus);
    } else {
      switch (getCurrent()) {
      case '$': case '@':
        result = scanIdentifier(delimStatus);
        break;
      case '.':
        result = scanNamedOperator(delimStatus);
        break;
      case '"':
        result = scanString(delimStatus);
        break;
      case '(':
        result = Token.newPunctuation(Type.LPAREN, delimStatus);
        advance();
        break;
      case ')':
        result = Token.newPunctuation(Type.RPAREN, delimStatus);
        advance();
        break;
      case '[':
        result = Token.newPunctuation(Type.LBRACK, delimStatus);
        advance();
        break;
      case ']':
        result = Token.newPunctuation(Type.RBRACK, delimStatus);
        advance();
        break;
      case '{':
        result = Token.newPunctuation(Type.LBRACE, delimStatus);
        advance();
        break;
      case '}':
        this.nextDelimStatus = DelimiterStatus.IMPLICIT;
        result = Token.newPunctuation(Type.RBRACE, delimStatus);
        advance();
        break;
      case ';':
        result = Token.newPunctuation(Type.SEMI, DelimiterStatus.EXPLICIT);
        advance();
        break;
      case ',':
        result = Token.newPunctuation(Type.COMMA, delimStatus);
        advance();
        break;
      default:
        result = new Token(Type.ERROR, Character.toString(getCurrent()),
            DelimiterStatus.NONE);
        advance();
        break;
      }
    }
    this.skipEther();
    return result;
  }

  /**
   * Advances over the current word token.
   */
  private Token scanWord(DelimiterStatus delimStatus) {
    int start = getCursor();
    while (hasMore() && isWordPart(getCurrent()))
      advance();
    if (hasMore() && getCurrent() == ':') {
      int end = getCursor();
      advance();
      String str = source.substring(start, end);
      return new TagToken(delimStatus, RString.of(str));
    } else {
      return new Token(Type.WORD, source.substring(start, getCursor()), delimStatus);
    }
  }

  private Token scanIdentifier(DelimiterStatus delimStatus) {
    boolean isDynamic = (getCurrent() == '$');
    int start = getCursor();
    advance();
    while (hasMore() && (isWordPart(getCurrent()) || (getCurrent() == ':')))
      advance();
    String value = source.substring(start, getCursor());
    List<ITagValue> parts = Factory.newArrayList();
    for (String part : value.substring(1).split(":"))
      parts.add(RString.of(part));
    return new Token.NameToken(value, delimStatus, isDynamic, Name.of(parts.toArray(new ITagValue[0])));
  }

  /**
   * Advances over the current number.
   */
  private Token scanNumber(DelimiterStatus delimStatus) {
    int start = getCursor();
    while (hasMore() && isNumberPart(getCurrent()))
      advance();
    int end = getCursor();
    String str = source.substring(start, end);
    int intValue = Integer.parseInt(str);
    ITagValue value = RInteger.of(intValue);
    if (hasMore() && getCurrent() == ':') {
      advance();
      return new TagToken(delimStatus, value);
    } else {
      return new LiteralToken(delimStatus, value);
    }
  }

  /**
   * Advances over the current operator;
   */
  private Token scanOperator(DelimiterStatus delimStatus) {
    int start = getCursor();
    while (hasMore() && isOperatorPart(getCurrent()))
      advance();
    return new Token(Type.OPERATOR, source.substring(start, getCursor()), delimStatus);
  }

  /**
   * Advances over the current named operator.
   */
  private Token scanNamedOperator(DelimiterStatus delimStatus) {
    Assert.equals('.', getCurrent());
    advance();
    int start = getCursor();
    while (hasMore() && isWordPart(getCurrent()))
      advance();
    return new Token(Type.OPERATOR, source.substring(start, getCursor()), delimStatus);
  }

  /**
   * Advances over the current string.
   */
  private Token scanString(DelimiterStatus delimStatus) {
    Assert.equals('"', getCurrent());
    advance();
    int start = getCursor();
    while (hasMore() && getCurrent() != '"')
      advance();
    if (hasMore())
      advance();
    return new Token(Type.STRING, source.substring(start, getCursor() - 1), delimStatus);
  }

  /**
   * Returns the tokens of the string held by this tokenizer.
   */
  private List<Token> tokenize() {
    List<Token> tokens = Factory.newArrayList();
    while (hasMore())
      tokens.add(scanNext());
    return tokens;
  }

  /**
   * Returns the tokens of the given input string.
   */
  public static List<Token> tokenize(String source) {
    return new Tokenizer(new StringCharStream(source)).tokenize();
  }

}
