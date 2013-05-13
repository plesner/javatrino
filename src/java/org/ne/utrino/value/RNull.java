package org.ne.utrino.value;

import java.util.Objects;

public class RNull extends RDeepImmutable {

  private static final RNull INSTANCE = new RNull();

  private RNull() { }

  public static RNull get() {
    return INSTANCE;
  }

  @Override
  public int objectHashCode() {
    return Objects.hashCode(this);
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return this == obj;
  }

}
