package org.ne.utrino.value;

import org.ne.utrino.runtime.Signature;

public class RMethod extends RDeepImmutable {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  private final Signature signature;

  public RMethod(Signature signature) {
    this.signature = signature;
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return this == obj;
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

  /**
   * Returns this method's signature.
   */
  public Signature getSignature() {
    return this.signature;
  }

}
