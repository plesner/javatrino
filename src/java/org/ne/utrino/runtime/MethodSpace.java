package org.ne.utrino.runtime;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.ne.utrino.util.Factory;
import org.ne.utrino.value.RProtocol;

/**
 * A helper object that keeps track of the inheritance graph and methods available
 * in a scope.
 */
public class MethodSpace {

  private final Map<RProtocol, Collection<RProtocol>> inheritance = Factory.newIdentityHashMap();
  private final Map<RProtocol, Collection<Method>> methods = Factory.newIdentityHashMap();



}
