package org.ne.utrino.value;

public class RNamespace extends RBuiltObject {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

}
