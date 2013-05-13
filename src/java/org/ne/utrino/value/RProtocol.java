package org.ne.utrino.value;

import java.util.Objects;
/**
 * An atomic protocol object. A protocol identifies an object as supporting a
 * particular set of messages. The protocol itself doesn't know what those
 * messages are, the method space has that information.
 */
public class RProtocol extends RDeepImmutable {

  @Override
  public int objectHashCode() {
    return Objects.hashCode(this);
  }

  @Override
  public boolean objectEquals(IValue obj) {
    return this == obj;
  }

}
