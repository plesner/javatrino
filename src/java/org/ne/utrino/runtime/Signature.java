package org.ne.utrino.runtime;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.ne.utrino.util.Factory;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;

/**
 * A method signature that can be used to match against a concrete set of
 * arguments.
 */
public class Signature {

  /**
   * A single
   */
  private static class Entry implements Comparable<Entry> {

    public final ITagValue tag;
    public final Guard guard;
    public final int index;

    public Entry(ITagValue tag, Guard guard, int index) {
      this.tag = tag;
      this.guard = guard;
      this.index = index;
    }

    @Override
    public int compareTo(Entry that) {
      return this.tag.compareTo(that.tag);
    }

  }

  /**
   * A builder type used to construct a signature.
   */
  public static class Builder {

    private final List<ParameterBuilder> params = Factory.newArrayList();
    private boolean allowExtra = false;

    public ParameterBuilder addParameter(Guard guard) {
      return addParameter(new ParameterBuilder(guard));
    }

    public ParameterBuilder addParameter(ParameterBuilder builder) {
      this.params.add(builder);
      return builder;
    }

    /**
     * Produces a signature object based on the input to this builder.
     */
    public Signature build() {
      List<Entry> entries = Factory.newArrayList();
      int index = 0;
      for (ParameterBuilder param : params) {
        for (ITagValue tag : param.tags)
          entries.add(new Entry(tag, param.guard, index));
        index++;
      }
      Collections.sort(entries);
      return new Signature(params.size(), entries, allowExtra);
    }

  }

  /**
   * A builder type used to construct a single signature parameter.
   */
  public static class ParameterBuilder {

    private final Guard guard;
    private final List<ITagValue> tags = Factory.newArrayList();
    private boolean isOptional = false;

    public ParameterBuilder(Guard guard) {
      this.guard = guard;
    }

    /**
     * Adds an entry to the set of tags that identify this parameter.
     */
    public ParameterBuilder addTag(ITagValue tag) {
      this.tags.add(tag);
      return this;
    }

    /**
     * Sets whether this parameter is optional.
     */
    public ParameterBuilder setOptional(boolean value) {
      this.isOptional = value;
      return this;
    }

  }

  private final List<ITagValue> tags;
  private final List<Entry> entries;
  private final int paramCount;
  private final boolean allowExtra;

  private Signature(int paramCount, List<Entry> entries, boolean allowExtra) {
    this.tags = buildTagList(entries);
    this.entries = entries;
    this.paramCount = paramCount;
    this.allowExtra = allowExtra;
  }

  /**
   * Builds a list of just the tags based on the list of entries.
   */
  private static List<ITagValue> buildTagList(List<Entry> entries) {
    List<ITagValue> tags = Factory.newArrayList();
    for (Entry entry : entries)
      tags.add(entry.tag);
    return tags;
  }

  /**
   * Returns a fresh signature builder.
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * The status of a match -- whether it succeeded and if not why.
   */
  public enum MatchResult {

    /**
     * There was an argument we didn't expect.
     */
    UNEXPECTED_ARGUMENT,

    /**
     * Multiple arguments were passed for the same parameter.
     */
    REDUNDANT_ARGUMENT,

    /**
     * This signature expected more arguments than were passed.
     */
    MISSING_ARGUMENTS,

    /**
     * A guard rejected an argument.
     */
    GUARD_REJECTED,

    /**
     * The invocation matched.
     */
    MATCH

  }

  /**
   * Matches the given invocation against this signature.
   */
  public MatchResult match(IInvocation record) {
    int recordEntryCount = record.getEntryCount();
    BitSet argsSeen = new BitSet(paramCount);
    int argsSeenCount = 0;
    // Scan through the arguments and look them up in the signature.
    for (int i = 0; i < recordEntryCount; i++) {
      ITagValue tag = record.getTag(i);
      IValue value = record.getValue(i);
      int entryIndex = Collections.binarySearch(tags, tag);
      if (entryIndex < 0) {
        // There was no signature entry corresponding to this tag. Fail.
        return MatchResult.UNEXPECTED_ARGUMENT;
      }
      Entry entry = entries.get(entryIndex);
      if (argsSeen.get(entry.index)) {
        // We've seen this entry before; fail.
        return MatchResult.REDUNDANT_ARGUMENT;
      } else if (Guard.isMatch(entry.guard.match(value))) {
        // The guard matches, keep going.
        argsSeen.set(entry.index);
        argsSeenCount++;
      } else {
        // The guard rejected; fail.
        return MatchResult.GUARD_REJECTED;
      }
    }
    if (argsSeenCount < paramCount) {
      // There are some parameters we haven't seen. Fail.
      return MatchResult.MISSING_ARGUMENTS;
    } else {
      // We saw all parameters and their guards approved. Match!
      return MatchResult.MATCH;
    }
  }

}
