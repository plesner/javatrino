package org.ne.utrino.value;

public class RInteger implements IValue {

  private final int value;

  public RInteger(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

}
