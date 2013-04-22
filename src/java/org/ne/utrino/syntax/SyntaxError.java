package org.ne.utrino.syntax;
/**
 * Exception that signals a syntax error.
 */
@SuppressWarnings("serial")
public class SyntaxError extends RuntimeException {

  private final Token token;

  public SyntaxError(Token token) {
    super(token.toString());
    this.token = token;
  }

  public Token getToken() {
    return token;
  }

}
