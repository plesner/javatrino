package org.ne.utrino.runtime;

import java.util.Collection;

import org.ne.utrino.value.RProtocol;

/**
 * A hierarchy can answer queries about the relations between protocols.
 */
public interface IHierarchy {

  /**
   * Returns the set of parent protocols for the given protocol.
   */
  public Collection<RProtocol> getParents(RProtocol protocol);

}
