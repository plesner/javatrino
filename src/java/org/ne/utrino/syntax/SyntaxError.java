package org.ne.utrino.syntax;
/**
 * Exception that signals a syntax error.
 */
@SuppressWarnings("serial")
public class SyntaxError extends Exception {

  private final IToken token;

  public SyntaxError(IToken token) {
    super(token.toString());
    this.token = token;
  }

  public IToken getToken() {
    return token;
  }

}
