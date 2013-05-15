package org.ne.utrino.testing;

import org.ne.utrino.value.IValue;
import org.ne.utrino.value.Phase;
import org.ne.utrino.value.Species;
/**
 * A dummy value with empty implementations of all methods.
 */
public class FakeValue implements IValue {

  @Override
  public Phase getPhase() {
    return Phase.DEEP_IMMUTABLE;
  }

  @Override
  public boolean trySetPhase(Phase phase) {
    return false;
  }

  @Override
  public boolean isIdentical(IValue other) {
    return this == other;
  }

  @Override
  public Species getSpecies() {
    return null;
  }

}
