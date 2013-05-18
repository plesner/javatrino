package org.ne.utrino.value;

import org.ne.utrino.util.Assert;


public class RString extends RDeepImmutable implements ITagValue {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  private final String value;

  public RString(String value) {
    this.value = Assert.notNull(value);
  }

  @Override
  public int compareTo(ITagValue that) {
    Flavor thatFlavor = that.getFlavor();
    if (thatFlavor != Flavor.STRING)
      return Flavor.STRING.compareTo(thatFlavor);
    return this.value.compareTo(((RString) that).value);
  }

  @Override
  public Flavor getFlavor() {
    return Flavor.STRING;
  }

  @Override
  public int hashCode() {
    return this.value.hashCode();
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return (obj instanceof RString)
        ? ((RString) obj).value.equals(value)
        : false;
  }

  @Override
  public String toString() {
    return "\"" + this.value + "\"";
  }

  @Override
  public boolean isIdentical(IValue other) {
    return this.equals(other);
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

  public static RProtocol getProtocol() {
    return PROTOCOL;
  }

  /**
   * Returns a new string object with the given value.
   */
  public static RString of(String value) {
    return new RString(value);
  }

}
