package org.ne.utrino.value;

import org.ne.utrino.runtime.MethodSpace;

public class RLambda extends RBuiltObject {

  private static final RProtocol PROTOCOL = new RProtocol("Lambda");
  private static final Species SPECIES = new Species(PROTOCOL);

  private final MethodSpace methodSpace;

  public RLambda(MethodSpace methodSpace) {
    this.methodSpace = methodSpace;
  }

  /**
   * Returns this lambda's internal method space.
   */
  public MethodSpace getMethodSpace() {
    return this.methodSpace;
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

  public static RProtocol getProtocol() {
    return PROTOCOL;
  }

  @Override
  public boolean trySetPhase(Phase phase) {
    boolean result = super.trySetPhase(phase);
    if (!getPhase().isMutable())
      methodSpace.ensureFrozen();
    return result;
  }

}
