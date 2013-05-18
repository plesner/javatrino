package org.ne.utrino.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
import org.ne.utrino.value.RMethod;
import org.ne.utrino.value.RProtocol;

/**
 * A helper object that keeps track of the inheritance graph and methods available
 * in a scope.
 */
public class MethodSpace implements IHierarchy {

  private boolean isFrozen = false;
  private final Map<RProtocol, Collection<RProtocol>> inheritance = Factory.newIdentityHashMap();
  private final Collection<RMethod> methods = Factory.newArrayList();
  private MethodLookup lookupCache = null;

  @Override
  public Collection<RProtocol> getParents(RProtocol protocol) {
    Assert.that(isFrozen);
    Collection<RProtocol> parents = inheritance.get(protocol);
    return (parents == null) ? Collections.<RProtocol>emptySet() : parents;
  }

  /**
   * Adds an inheritance relationship between the given subtype protocol and
   * supertype protocol.
   */
  public void addInheritance(RProtocol subProto, RProtocol superProto) {
    Assert.that(!isFrozen);
    Collection<RProtocol> supers = inheritance.get(subProto);
    if (supers == null) {
      supers = Factory.newArrayList();
      inheritance.put(subProto, supers);
    }
    supers.add(superProto);
  }

  /**
   * Looks up the most appropriate method for the given invocation.
   */
  public RMethod lookupMethod(IInvocation args) {
    Assert.that(isFrozen);
    MethodLookup lookup = grabLookup(args.getEntryCount());
    lookup.findMethod(args, methods, this);
    RMethod result = lookup.getMethod();
    returnLookup(lookup);
    return result;
  }

  /**
   * Adds another method to the collection.
   */
  public void addMethod(RMethod method) {
    Assert.that(!isFrozen);
    this.methods.add(method);
  }

  /**
   * Freezes this method space if it isn't frozen already.
   */
  public void ensureFrozen() {
    this.isFrozen = true;
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
