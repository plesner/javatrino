package org.ne.utrino.value;

public class RBool extends RDeepImmutable {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  private static final RBool TRUE = new RBool(true);
  private static final RBool FALSE = new RBool(false);

  private final boolean value;

  private RBool(boolean value) {
    this.value = value;
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return (this == obj);
  }

  /**
   * Returns the singleton boolean that represents the given value.
   */
  public static RBool of(boolean value) {
    return value ? getTrue() : getFalse();
  }

  /**
   * Returns the singleton boolean that represents true.
   */
  public static RBool getTrue() {
    return TRUE;
  }

  /**
   * Returns the singleton boolean that represents false.
   */
  public static RBool getFalse() {
    return FALSE;
  }

  @Override
  public String toString() {
    return value ? "#<true>" : "#<false>";
  }

}
