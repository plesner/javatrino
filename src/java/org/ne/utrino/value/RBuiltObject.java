package org.ne.utrino.value;

import java.util.Objects;


/**
 * An object which has already been built but which can still be mutable or
 * immutable.
 */
public abstract class RBuiltObject extends RObject {

  private Phase phase = Phase.MUTABLE;

  @Override
  public Phase getPhase() {
    return this.phase;
  }

  /**
   * Advances the phase of this object to the given phase.
   */
  @Override
  public boolean trySetPhase(Phase phase) {
    if (getPhase().allowPhaseChange(phase)) {
      this.phase = phase;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int objectHashCode() {
    return Objects.hashCode(this);
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return (this == obj);
  }

}
