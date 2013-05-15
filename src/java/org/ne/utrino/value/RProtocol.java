package org.ne.utrino.value;

/**
 * An atomic protocol object. A protocol identifies an object as supporting a
 * particular set of messages. The protocol itself doesn't know what those
 * messages are, the method space has that information.
 */
public class RProtocol extends RDeepImmutable {

  private static final RProtocol PROTOCOL = new RProtocol("Protocol");
  private static final Species SPECIES = new Species(PROTOCOL);

  private final String debugName;

  public RProtocol(String debugName) {
    this.debugName = debugName;
  }

  public RProtocol() {
    this(null);
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return this == obj;
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

  @Override
  public String toString() {
    return "#<protocol " + (debugName == null ? ("#" + hashCode()) : debugName) + ">";
  }

}
