package org.ne.utrino.testing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ne.utrino.runtime.Guard;
import org.ne.utrino.runtime.IHierarchy;
import org.ne.utrino.runtime.IInvocation;
import org.ne.utrino.runtime.Signature;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Pair;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RInteger;
import org.ne.utrino.value.RNull;
import org.ne.utrino.value.RObject;
import org.ne.utrino.value.RProtocol;
import org.ne.utrino.value.RString;
import org.ne.utrino.value.Species;

/**
 * Convenience factory methods used in tests.
 */
public class TestFactory {

  /**
   * Converts an object to a tag value. For instance, wraps integers and strings
   * in objects.
   */
  public static ITagValue toTag(Object obj) {
    if (obj instanceof Integer) {
      return RInteger.newInt((Integer) obj);
    } else if (obj instanceof String) {
      return new RString((String) obj);
    } else {
      Assert.that(obj instanceof ITagValue);
      return (ITagValue) obj;
    }
  }

  /**
   * Converts an object to a value. For instance, wraps integers and strings in
   * neutrino objects.
   */
  public static IValue toValue(Object obj) {
    if (obj instanceof IValue) {
      return (IValue) obj;
    } else if (obj == null) {
      return RNull.get();
    } else {
      return toTag(obj);
    }
  }

  /**
   * Creates a new hierarchy entry that can be passed to {@link #newHierarchy()}.
   */
  public static Pair<RProtocol, List<RProtocol>> entry(RProtocol proto,
      RProtocol... parents) {
    return Pair.of(proto, Arrays.asList(parents));
  };

  /**
   * Creates a new hierarchy containing the given entries.
   */
  @SafeVarargs
  public static IHierarchy newHierarchy(Pair<RProtocol, List<RProtocol>>... pairs) {
    final Map<RProtocol, List<RProtocol>> hierarchy = Factory.newHashMap();
    for (Pair<RProtocol, List<RProtocol>> pair : pairs)
      hierarchy.put(pair.getFirst(), pair.getSecond());
    return new IHierarchy() {
      @Override
      public Collection<RProtocol> getParents(RProtocol protocol) {
        List<RProtocol> result = hierarchy.get(protocol);
        return (result == null) ? Collections.<RProtocol>emptySet() : result;
      }
    };
  }

  /**
   * Returns a new value whose primary protocol is the given value.
   */
  public static IValue withProtocol(RProtocol protocol) {
    final Species species = new Species(protocol);
    return new FakeValue() {
      @Override
      public Species getSpecies() {
        return species;
      }
    };
  }

  /**
   * Shorthand for creating an array of integers.
   */
  public static int[] ints(int... values) {
    return values;
  }

  // Shorthands for protocols.
  public static final RProtocol INT_P = RInteger.getProtocol();
  public static final RProtocol STR_P = RString.getProtocol();
  public static final RProtocol OBJ_P = RObject.getProtocol();

  /**
   * Creates an argument pair.
   */
  public static Pair<ITagValue, IValue> arg(Object tag, Object value) {
    return Pair.of(toTag(tag), toValue(value));
  }

  /**
   * Creates an invocation from alternating tags and values.
   */
  @SafeVarargs
  public static IInvocation newInvocation(Pair<ITagValue, IValue>... pairs) {
    final List<Pair<ITagValue, IValue>> args = Arrays.asList(pairs);
    Collections.sort(args, Pair.<ITagValue, IValue>firstComparator());
    return new IInvocation() {
      @Override
      public IValue getValue(int index) {
        return args.get(index).getSecond();
      }
      @Override
      public ITagValue getTag(int index) {
        return args.get(index).getFirst();
      }
      @Override
      public int getEntryCount() {
        return args.size();
      }
    };
  }

  /**
   * Creates a tagged parameter.
   */
  public static Signature.ParameterBuilder param(Guard guard, boolean isOptional, Object... tags) {
    Signature.ParameterBuilder builder = new Signature.ParameterBuilder(guard)
        .setOptional(isOptional);
    for (Object tag : tags)
      builder.addTag(toTag(tag));
    return builder;
  }

  /**
   * Combines a set of parameters into a signature.
   */
  public static Signature newSignature(boolean allowExtra, Signature.ParameterBuilder... params) {
    Signature.Builder builder = Signature
        .newBuilder()
        .setAllowExtra(allowExtra);
    for (Signature.ParameterBuilder param : params)
      builder.addParameter(param);
    return builder.build();
  }


}
