package org.ne.utrino.value;
/**
 * Convenience supertype that can be used with objects that are by nature deeply
 * immutable.
 */
public abstract class RDeepImmutable extends RObject {

  @Override
  public Phase getPhase() {
    return Phase.DEEP_IMMUTABLE;
  }

  @Override
  public boolean trySetPhase(Phase phase) {
    return false;
  }

}
