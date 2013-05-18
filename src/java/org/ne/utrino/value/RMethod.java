package org.ne.utrino.value;

import org.ne.utrino.interpreter.CodeBlock;
import org.ne.utrino.runtime.Signature;

public class RMethod extends RDeepImmutable {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  private final Signature signature;
  private final CodeBlock code;

  public RMethod(Signature signature, CodeBlock code) {
    this.signature = signature;
    this.code = code;
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

  /**
   * Returns this method's implementation.
   */
  public CodeBlock getCode() {
    return this.code;
  }

}
