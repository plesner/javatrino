package org.ne.utrino.syntax;

import java.util.List;

import org.ne.utrino.syntax.IToken.Type;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Internal;

/**
 * Utility for chopping a string into tokens.
 */
public class Tokenizer<T> {

  private final ICharStream source;
  private final ITokenFactory<T> tokenFactory;

  public Tokenizer(ICharStream source, ITokenFactory<T> tokenFactory) {
    this.source = source;
    this.tokenFactory = tokenFactory;
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
   * Returns true if we're at the second character of a pair (that is,
   * we're not at the very beginning or end) and that pair is different
   * from the specified values.
   */
  private boolean atDifferentPair(char first, char second) {
    return hasMore() && ((getCurrent() != first) || (source.getNext() != second));
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

  /**
   * Advances over the next token or tokens, adding them to the given
   * list.
   */
  public T scanNext() {
    Assert.that(hasMore());
    T result;
    if (isNewline(getCurrent())) {
      result = tokenFactory.newNewline(getCurrent());
      advance();
    } else if (isSpace(getCurrent())) {
      result = scanSpace();
    } else if (isWordStart(getCurrent())) {
      result = scanWord();
    } else if (isNumberStart(getCurrent())) {
      result = scanNumber();
    } else if (isOperatorStart(getCurrent())) {
      result = scanOperator();
    } else {
      switch (getCurrent()) {
      case '$':
        result = scanIdentifier();
        break;
      case '.':
        result = scanNamedOperator();
        break;
      case '"':
        result = scanString();
        break;
      case '(':
        result = tokenFactory.newPunctuation(Type.LPAREN);
        advance();
        break;
      case ')':
        result = tokenFactory.newPunctuation(Type.RPAREN);
        advance();
        break;
      case '[':
        result = tokenFactory.newPunctuation(Type.LBRACK);
        advance();
        break;
      case ']':
        result = tokenFactory.newPunctuation(Type.RBRACK);
        advance();
        break;
      case '{':
        result = tokenFactory.newPunctuation(Type.LBRACE);
        advance();
        break;
      case '}':
        result = tokenFactory.newPunctuation(Type.RBRACE);
        advance();
        break;
      case ';':
        result = tokenFactory.newPunctuation(Type.SEMI);
        advance();
        break;
      case ',':
        result = tokenFactory.newPunctuation(Type.COMMA);
        advance();
        break;
      case '#':
        advance();
        switch (getCurrent()) {
        case '#': case '@': case '-':
          result = scanEndOfLineComment(getCursor() - 1);
          break;
        case '{':
          result = scanBlockComment(getCursor() - 1);
          break;
        default:
          result = tokenFactory.newPunctuation(Type.HASH);
          break;
        }
        break;
      case '@':
        result = tokenFactory.newPunctuation(Type.AT);
        advance();
        break;
      default:
        result = tokenFactory.newError(getCurrent());
        advance();
        break;
      }
    }
    return result;
  }

  private T scanSpace() {
    int start = getCursor();
    while (hasMore() && isSpaceNotNewline(getCurrent()))
      advance();
    return tokenFactory.newSpace(source.substring(start, getCursor()));
  }

  private T scanEndOfLineComment(int start) {
    while (hasMore() && !isNewline(getCurrent()))
      advance();
    return tokenFactory.newComment(source.substring(start, getCursor()));
  }

  private T scanBlockComment(int start) {
    int lineStart = start;
    while (atDifferentPair('}', '#'))
      advance();
    // If we reached the end we're at the final '#' so we advance past
    // it.
    if (hasMore())
      advance();
    if (hasMore())
      advance();
    return tokenFactory.newComment(source.substring(lineStart, getCursor()));
  }

  /**
   * Advances over the current word token.
   */
  private T scanWord() {
    int start = getCursor();
    while (hasMore() && isWordPart(getCurrent()))
      advance();
    if (hasMore() && getCurrent() == ':') {
      int end = getCursor();
      advance();
      return tokenFactory.newKeyword(source.substring(start, end));
    } else {
      return tokenFactory.newWord(source.substring(start, getCursor()));
    }
  }

  private T scanIdentifier() {
    Assert.equals('$', getCurrent());
    advance();
    int start = getCursor();
    while (hasMore() && isWordPart(getCurrent()))
      advance();
    return tokenFactory.newIdentifier(source.substring(start, getCursor()));
  }

  /**
   * Advances over the current number.
   */
  private T scanNumber() {
    int start = getCursor();
    while (hasMore() && isNumberPart(getCurrent()))
      advance();
    return tokenFactory.newNumber(source.substring(start, getCursor()));
  }

  /**
   * Advances over the current operator;
   */
  private T scanOperator() {
    int start = getCursor();
    while (hasMore() && isOperatorPart(getCurrent()))
      advance();
    return tokenFactory.newOperator(source.substring(start, getCursor()));
  }

  /**
   * Advances over the current named operator.
   */
  private T scanNamedOperator() {
    Assert.equals('.', getCurrent());
    advance();
    int start = getCursor();
    while (hasMore() && isWordPart(getCurrent()))
      advance();
    return tokenFactory.newOperator(source.substring(start, getCursor()));
  }
  
  /**
   * Advances over the current string.
   */
  private T scanString() {
    Assert.equals('"', getCurrent());
    advance();
    int start = getCursor();
    while (hasMore() && getCurrent() != '"')
      advance();
    if (hasMore())
      advance();
    return tokenFactory.newString(source.substring(start, getCursor() - 1));
  }
  
  /**
   * Returns the tokens of the string held by this tokenizer.
   */
  private List<T> tokenize() {
    List<T> tokens = Factory.newArrayList();
    while (hasMore())
      tokens.add(scanNext());
    return tokens;
  }

  /**
   * Returns the tokens of the given input string.
   */
  public static <T extends IToken> List<T> tokenize(String source, ITokenFactory<T> tokenFactory) {
    return new Tokenizer<T>(new StringCharStream(source), tokenFactory).tokenize();
  }

}
