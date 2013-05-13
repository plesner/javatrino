package org.ne.utrino.value;

import org.ne.utrino.util.Assert;


public class RString extends RDeepImmutable implements ITagValue {

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
  public int objectHashCode() {
    return this.value.hashCode();
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return (obj instanceof RString)
        ? ((RString) obj).value.equals(value)
        : false;
  }

  @Override
  public boolean isIdentical(IValue other) {
    return this.equals(other);
  }

}
