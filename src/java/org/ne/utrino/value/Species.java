package org.ne.utrino.value;


/**
 * A species identifies the type of an object and encapsulates meta-information
 * about that type of object. It is not the same as an object's protocol, which
 * is a surface-level construct. A species is an implementation detail that is
 * transparent to the surface language.
 */
public class Species {

  private final RProtocol primary;

  /**
   * Creates a new descriptor
   * @param protocols
   */
  public Species(RProtocol primary) {
    this.primary = primary;
  }

  /**
   * Returns the primary protocol supported by this kind of object.
   */
  public RProtocol getPrimary() {
    return this.primary;
  }

}
