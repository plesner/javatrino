package org.ne.utrino.value;
/**
 * A 32-bit fixed integer.
 */
public class RInteger extends RObject {

  private final int value;

  public RInteger(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

  @Override
  public Phase getPhase() {
    return Phase.DEEP_IMMUTABLE;
  }

  @Override
  public int objectHashCode() {
    return this.value;
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return (obj instanceof RInteger)
        ? ((RInteger) obj).value == value
        : false;
  }

}
