package org.ne.utrino.value;
/**
 * Dumb immutable data available to the implementation but which must not be
 * exposed to the surface language.
 */
public class RInternalData extends RDeepImmutable {

  @Override
  public Species getSpecies() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return (this == obj);
  }

}
