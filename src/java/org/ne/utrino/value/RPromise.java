package org.ne.utrino.value;

import org.ne.utrino.util.Assert;

/**
 * A deferred promise value.
 */
public class RPromise<T extends IValue> extends RBuiltObject {

  public enum State {
    EMPTY,
    SUCCESS,
    FAILURE
  }

  private T value = null;
  private State state = State.EMPTY;
  private Phase phase = Phase.MUTABLE;

  /**
   * Sets the value of this promise. If the value has already been set this is
   * ignored.
   */
  public void fulfill(T value) {
    if (this.state != State.EMPTY)
      return;
    Assert.that(this.getPhase().isMutable());
    this.value = value;
    this.state = State.SUCCESS;
    this.setPhase(Phase.SHALLOW_IMMUTABLE);
  }

  /**
   * Convenience factory constructor that give you an empty promise.
   */
  public static <T extends IValue> RPromise<T> empty() {
    return new RPromise<T>();
  }

}
