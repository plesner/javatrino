package org.ne.utrino.syntax;

import java.util.Objects;

import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Name;

/**
 * A single token within some source code.
 */
public class Token {

  /**
   * The flavor of a token -- for instance space and punctuation, of
   * which there are multiple types but all share various properties.
   */
  public enum Flavor {
    ETHER,
    PUNCTUATION,
    OTHER
  }

  /**
   * The different classes of tokens.
   */
  public enum Type {

    WORD      (null, "word",        Flavor.OTHER),
    KEYWORD   (null, "keyword",     Flavor.OTHER),
    STRING    (null, "string",      Flavor.OTHER),
    OPERATOR  (null, "operator",    Flavor.OTHER),
    ERROR     (null, "error",       Flavor.OTHER),
    EOF       (null, "eof",         Flavor.OTHER),
    NUMBER    (null, "number",      Flavor.OTHER),
    IDENTIFIER(null, "identifier",  Flavor.OTHER),
    LPAREN    ("(",  "punctuation", Flavor.PUNCTUATION),
    RPAREN    (")",  "punctuation", Flavor.PUNCTUATION),
    LBRACK    ("[",  "punctuation", Flavor.PUNCTUATION),
    RBRACK    ("]",  "punctuation", Flavor.PUNCTUATION),
    LBRACE    ("{",  "punctuation", Flavor.PUNCTUATION),
    RBRACE    ("}",  "punctuation", Flavor.PUNCTUATION),
    SEMI      (";",  "punctuation", Flavor.PUNCTUATION),
    COMMA     (",",  "punctuation", Flavor.PUNCTUATION),
    HASH      ("#",  "punctuation", Flavor.PUNCTUATION),
    AT        ("@",  "punctuation", Flavor.PUNCTUATION);

    private final Flavor flavor;
    private final String value;
    private final String category;

    private Type(String value, String category, Flavor flavor) {
      this.flavor = flavor;
      this.value = value;
      this.category = category;
    }

    @Override
    public String toString() {
      return this.value;
    }

    public String getCategory() {
      return this.category;
    }

    public Flavor getFlavor() {
      return this.flavor;
    }

    public boolean isPunctuation() {
      return getFlavor() == Flavor.PUNCTUATION;
    }

  }

  /**
   * How a token behaves when considered as a delimiter.
   */
  public enum DelimiterStatus {

    /**
     * This token is not a delimiter in any way.
     */
    NONE(false, false),

    /**
     * This token can be an implicit delimiter but is not a delimiter in itself.
     */
    IMPLICIT(true, false),

    /**
     * This token functions only as a delimiter.
     */
    EXPLICIT(true, true);

    private final boolean isDelimiter;
    private final boolean isExplicit;

    private DelimiterStatus(boolean isDelimiter, boolean isExplicit) {
      this.isDelimiter = isDelimiter;
      this.isExplicit = isExplicit;
    }

    /**
     * Does this token function as a delimiter?
     */
    public boolean isDelimiter() {
      return this.isDelimiter;
    }

    /**
     * Is this token's function only to be a delimiter?
     */
    public boolean isExplicit() {
      return this.isExplicit;
    }

  }

  private final Type type;
  private final String value;
  private final DelimiterStatus delimStatus;

  protected Token(Type type, String value, DelimiterStatus delimStatus) {
    Assert.that(value == null || value.length() > 0);
    this.type = type;
    this.value = value;
    this.delimStatus = delimStatus;
  }

  public String getCategory() {
    return type.getCategory();
  }

  public DelimiterStatus getDelimiterStatus() {
    return this.delimStatus;
  }

  public boolean is(Type type) {
    return this.type == type;
  }

  public boolean is(Flavor flavor) {
    return this.type.getFlavor() == flavor;
  }

  public Type getType() {
    return this.type;
  }

  public String getValue() {
    return this.value;
  }

  public Name getName() {
    throw new UnsupportedOperationException();
  }

  public boolean isDynamic() {
    throw new UnsupportedOperationException();
  }

  public static Token newPunctuation(Type type, DelimiterStatus delimStatus) {
    Assert.that(type.isPunctuation());
    return new Token(type, type.toString(), delimStatus);
  }

  @Override
  public int hashCode() {
    return type.hashCode() ^ (value == null ? 0 : value.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (!(obj instanceof Token)) {
      return false;
    } else {
      Token that = (Token) obj;
      if (that.type != this.type) {
        return false;
      } else {
        return Objects.equals(this.value, that.value) &&
               (this.delimStatus == that.delimStatus);
      }
    }
  }

  @Override
  public String toString() {
    return type.name() + "(" + this.value + ")";
  }

  public static class NameToken extends Token {

    private final boolean isDynamic;
    private final Name name;

    public NameToken(String value, DelimiterStatus delimStatus, boolean isDynamic, Name name) {
      super(Type.IDENTIFIER, value, delimStatus);
      this.isDynamic = isDynamic;
      this.name = name;
    }

    @Override
    public Name getName() {
      return this.name;
    }

    @Override
    public boolean isDynamic() {
      return this.isDynamic;
    }

  }

}
