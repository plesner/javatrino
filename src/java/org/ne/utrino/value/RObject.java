package org.ne.utrino.value;
/**
 * Default implementation of the value interface.
 */
public abstract class RObject implements IValue {

  private static final RProtocol PROTOCOL = new RProtocol("Object");

  /**
   * Is this object equal to the given one?
   */
  public abstract boolean objectEquals(IValue obj);

  @Override
  public final boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else {
      return (obj instanceof IValue) ? this.objectEquals((IValue) obj) : false;
    }
  }

  @Override
  public boolean isIdentical(IValue other) {
    return this == other;
  }

  public static RProtocol getProtocol() {
    return PROTOCOL;
  }

}
