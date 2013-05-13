package org.ne.utrino.value;

import java.util.Objects;

import org.ne.utrino.runtime.Signature;

public class RMethod extends RDeepImmutable {

  private final Signature signature;

  public RMethod(Signature signature) {
    this.signature = signature;
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
