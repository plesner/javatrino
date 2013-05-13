package org.ne.utrino.value;

import java.util.Objects;

/**
 * A custom object instance that starts out being under construction.
 */
public class RInstance extends RObject {

  private Phase phase = Phase.BUILDING;
  private Species desc;

  @Override
  public Phase getPhase() {
    return phase;
  }

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
    return this == obj;
  }

}
