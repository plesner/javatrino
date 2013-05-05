package org.ne.utrino.ast;
/**
 * A name used to identify something in a namespace.
 */
public interface ISymbol {

  public enum Era {
    PAST,
    CURRENT
  }

  /**
   * Returns the era this symbol belongs to.
   */
  public Era getEra();

}
