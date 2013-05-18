package org.ne.utrino.value;

import java.util.Map;

import org.ne.utrino.runtime.MethodSpace;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
/**
 * A combined name- and methodspace.
 */
public class RContext extends RBuiltObject {

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  private final Map<IValue, IValue> namespace = Factory.newHashMap();
  private final MethodSpace methodSpace = new MethodSpace();

  /**
   * Returns this context's method space.
   */
  public MethodSpace getMethodSpace() {
    return this.methodSpace;
  }

  /**
   * Binds the given key to the given value in this namespace.
   */
  public void set(IValue key, IValue value) {
    Assert.that(this.getPhase().isMutable());
    namespace.put(key, value);
  }

  @Override
  public boolean trySetPhase(Phase phase) {
    boolean result = super.trySetPhase(phase);
    if (result && !this.getPhase().isMutable())
      methodSpace.ensureFrozen();
    return result;
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

}
