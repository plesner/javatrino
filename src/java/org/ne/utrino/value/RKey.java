package org.ne.utrino.value;


/**
 * A unique key.
 */
public class RKey extends RDeepImmutable implements ITagValue {

  /**
   * The receiver argument key.
   */
  public static final RKey THIS = new RKey("this");

  /**
   * The method name argument key.
   */
  public static final RKey NAME = new RKey("name");

  private static final RProtocol PROTOCOL = new RProtocol();
  private static final Species SPECIES = new Species(PROTOCOL);

  private static int nextId = 0;
  private final int id;
  private final String debugName;

  public RKey(String debugName) {
    this.id = getNextId();
    this.debugName = (debugName == null) ? ("#" + this.id) : ("%" + debugName);
  }

  /**
   * Returns the next unique key id.
   */
  private static synchronized int getNextId() {
    return nextId++;
  }

  @Override
  public Species getSpecies() {
    return SPECIES;
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return (this == obj);
  }

  @Override
  public int hashCode() {
    return ~id;
  }

  @Override
  public int compareTo(ITagValue that) {
    Flavor thatFlavor = that.getFlavor();
    if (thatFlavor != Flavor.KEY)
      return Flavor.KEY.compareTo(thatFlavor);
    return Integer.compare(this.id, ((RKey) that).id);
  }

  @Override
  public Flavor getFlavor() {
    return Flavor.KEY;
  }

  @Override
  public String toString() {
    return this.debugName;
  }

}
