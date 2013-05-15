package org.ne.utrino.value;


public class RNull extends RDeepImmutable {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);
  private static final RNull INSTANCE = new RNull();

  private RNull() { }

  public static RNull get() {
    return INSTANCE;
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return this == obj;
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

}
