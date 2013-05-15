package org.ne.utrino.value;

import java.util.Map;

import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
/**
 * A combined name- and methodspace.
 */
public class RSpace extends RBuiltObject {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  private final Map<IValue, IValue> namespace = Factory.newHashMap();

  /**
   * Binds the given key to the given value in this namespace.
   */
  public void set(IValue key, IValue value) {
    Assert.that(this.getPhase().isMutable());
    namespace.put(key, value);
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

}
