package org.ne.utrino.value;
/**
 * Identifies the mutability state of an object.
 */
public enum Phase {

  BUILDING(true, true, false),
  MUTABLE(false, true, false),
  SHALLOW_IMMUTABLE(false, false, false),
  DEEP_IMMUTABLE(false, false, true);

  private final boolean isConstructing;
  private final boolean isMutable;
  private final boolean isDeepFrozen;

  private Phase(boolean isConstructing, boolean isMutable, boolean isDeepFrozen) {
    this.isConstructing = isConstructing;
    this.isMutable = isMutable;
    this.isDeepFrozen = isDeepFrozen;
  }

  /**
   * Does this object allow continued construction?
   */
  public boolean isConstructing() {
    return this.isConstructing;
  }

  /**
   * Does this object allow mutation?
   */
  public boolean isMutable() {
    return this.isMutable;
  }

  /**
   * Is this object and all objects reachable from it immutable?
   */
  public boolean isDeepFrozen() {
    return this.isDeepFrozen;
  }

  /**
   * Returns true if it is legal for an object to progress from this phase to
   * the given one. If this is equal to the given phase the "change" is
   * trivially allowed.
   */
  public boolean allowPhaseChange(Phase that) {
    return this.ordinal() <= that.ordinal();
  }

}
