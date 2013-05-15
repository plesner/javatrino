package org.ne.utrino.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.ne.utrino.util.Factory;
import org.ne.utrino.value.RMethod;
import org.ne.utrino.value.RProtocol;

/**
 * A helper object that keeps track of the inheritance graph and methods available
 * in a scope.
 */
public class MethodSpace implements IHierarchy {

  private final Map<RProtocol, Collection<RProtocol>> inheritance = Factory.newIdentityHashMap();
  private final Collection<RMethod> methods = Factory.newArrayList();

  @Override
  public Collection<RProtocol> getParents(RProtocol protocol) {
    Collection<RProtocol> parents = inheritance.get(protocol);
    return (parents == null) ? Collections.<RProtocol>emptySet() : parents;
  }

}
