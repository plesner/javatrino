package org.ne.utrino.value;
/**
 * A 32-bit fixed integer.
 */
public class RInteger extends RDeepImmutable implements ITagValue {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  private final int value;

  public RInteger(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

  @Override
  public int hashCode() {
    return this.value;
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return (obj instanceof RInteger)
        ? ((RInteger) obj).value == value
        : false;
  }

  @Override
  public boolean isIdentical(IValue other) {
    return this.equals(other);
  }

  @Override
  public Flavor getFlavor() {
    return Flavor.INTEGER;
  }

  @Override
  public int compareTo(ITagValue that) {
    Flavor thatFlavor = that.getFlavor();
    if (thatFlavor != Flavor.INTEGER)
      return Flavor.INTEGER.compareTo(thatFlavor);
    return Integer.compare(this.value, ((RInteger) that).value);
  }

  /**
   * Returns a new integer with the given value.
   */
  public static RInteger newInt(int value) {
    return new RInteger(value);
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

  public static RProtocol getProtocol() {
    return PROTOCOL;
  }

}
