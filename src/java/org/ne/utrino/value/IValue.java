package org.ne.utrino.value;
/**
 * Interface for all runtime values.
 */
public interface IValue {

  /**
   * Returns the current phase of this value.
   */
  public Phase getPhase();

  /**
   * Attempt to set the phase of this value. Returns true if setting succeeded.
   */
  public boolean trySetPhase(Phase phase);

  /**
   * Returns true if this value is identical to the other value. This is object
   * identity for most types and value identity for strings and integers.
   */
  public boolean isIdentical(IValue other);

}
