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
  private MethodLookup lookupCache = null;

  @Override
  public Collection<RProtocol> getParents(RProtocol protocol) {
    Collection<RProtocol> parents = inheritance.get(protocol);
    return (parents == null) ? Collections.<RProtocol>emptySet() : parents;
  }

  /**
   * Looks up the most appropriate method for the given invocation.
   */
  public RMethod lookupMethod(IInvocation args) {
    MethodLookup lookup = grabLookup(args.getEntryCount());
    lookup.findMethod(args, methods, this);
    RMethod result = lookup.getMethod();
    returnLookup(lookup);
    return result;
  }

  /**
   * Returns a lookup suitable for looking up an invocation with the give number
   * of arguments.
   */
  private synchronized MethodLookup grabLookup(int argCount) {
    if (lookupCache != null && lookupCache.getMaxArguments() <= argCount) {
      MethodLookup result = lookupCache;
      lookupCache = null;
      return result;
    }
    return new MethodLookup(argCount * 2);
  }

  /**
   * Caches the given lookup such that it can be reused by future lookups.
   */
  private synchronized void returnLookup(MethodLookup lookup) {
    if (lookupCache == null || lookupCache.getMaxArguments() < lookup.getMaxArguments())
      lookupCache = lookup;
  }

}
