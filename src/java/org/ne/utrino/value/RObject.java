package org.ne.utrino.value;
/**
 * Default implementation of the value interface.
 */
public abstract class RObject implements IValue {

  /**
   * Calculates this object's local hash code.
   */
  public abstract int objectHashCode();

  /**
   * Is this object equal to the given one?
   */
  public abstract boolean objectEquals(IValue obj);

  @Override
  public final int hashCode() {
    return this.objectHashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else {
      return (obj instanceof IValue) ? this.objectEquals((IValue) obj) : false;
    }
  }

}
