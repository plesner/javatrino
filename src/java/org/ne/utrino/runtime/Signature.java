package org.ne.utrino.runtime;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;

/**
 * A method signature that can be used to match against a concrete set of
 * arguments.
 */
public class Signature {

  /**
   * A single signature entry.
   */
  private static class Entry implements Comparable<Entry> {

    public final ITagValue tag;
    public final Guard guard;
    public final boolean isOptional;
    public final int index;

    public Entry(ITagValue tag, Guard guard, boolean isOptional, int index) {
      this.tag = tag;
      this.guard = guard;
      this.isOptional = isOptional;
      this.index = index;
    }

    @Override
    public int compareTo(Entry that) {
      return this.tag.compareTo(that.tag);
    }

    @Override
    public String toString() {
      return tag + "/" + index + ": " + guard;
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
      int mandatoryCount = 0;
      for (ParameterBuilder param : params) {
        if (!param.isOptional)
          mandatoryCount++;
        for (ITagValue tag : param.tags)
          entries.add(new Entry(tag, param.guard, param.isOptional, index));
        index++;
      }
      Collections.sort(entries);
      return new Signature(params.size(), mandatoryCount, entries, allowExtra);
    }

    /**
     * Does this signature allow extra arguments to be passed?
     */
    public Builder setAllowExtra(boolean value) {
      this.allowExtra = value;
      return this;
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

  /**
   * The status of a match -- whether it succeeded and if not why.
   */
  public enum MatchResult {

    /**
     * There was an argument we didn't expect.
     */
    UNEXPECTED_ARGUMENT(false),

    /**
     * Multiple arguments were passed for the same parameter.
     */
    REDUNDANT_ARGUMENT(false),

    /**
     * This signature expected more arguments than were passed.
     */
    MISSING_ARGUMENT(false),

    /**
     * A guard rejected an argument.
     */
    GUARD_REJECTED(false),

    /**
     * The invocation matched.
     */
    MATCH(true),

    /**
     * The invocation matched but had extra arguments which this signature allows.
     */
    EXTRA_MATCH(true);

    private final boolean didMatch;

    private MatchResult(boolean didMatch) {
      this.didMatch = didMatch;
    }

    /**
     * Does this status signify that the match was successful?
     */
    public boolean didMatch() {
      return this.didMatch;
    }

  }

  private final List<ITagValue> tags;
  private final List<Entry> entries;
  private final int totalParamCount;
  private final int mandatoryParamCount;
  private final boolean allowExtra;

  private Signature(int totalParamCount, int mandatoryParamCount, List<Entry> entries,
      boolean allowExtra) {
    this.tags = buildTagList(entries);
    this.entries = entries;
    this.totalParamCount = totalParamCount;
    this.mandatoryParamCount = mandatoryParamCount;
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
   * Returns the number of distinct parameters, for each of which there may be
   * multiple matching tags.
   */
  public int getParameterCount() {
    return this.totalParamCount;
  }

  /**
   * Matches the given invocation against this signature. You should not base
   * behavior on the exact failure type returned since there can be multiple
   * failures and the choice of which one gets returned is arbitrary.
   *
   * The scores array must be long enough to hold a score for each argument
   * in the invocation. If the match succeeds it holds the scores, if it fails
   * the state is unspecified.
   */
  public MatchResult match(IInvocation record, IHierarchy hierarchy, int[] scores) {
    int recordEntryCount = record.getEntryCount();
    Assert.that(scores.length >= recordEntryCount);
    BitSet paramsSeen = new BitSet(totalParamCount);
    int mandatoryArgsSeenCount = 0;
    MatchResult onMatch = MatchResult.MATCH;
    for (int i = 0; i < recordEntryCount; i++)
      scores[i] = Guard.NO_MATCH;
    // Scan through the arguments and look them up in the signature.
    for (int i = 0; i < recordEntryCount; i++) {
      ITagValue tag = record.getTag(i);
      IValue value = record.getValue(i);
      int entryIndex = Collections.binarySearch(tags, tag);
      if (entryIndex < 0) {
        if (allowExtra) {
          onMatch = MatchResult.EXTRA_MATCH;
          scores[i] = Guard.EXTRA_MATCH;
          continue;
        } else {
          // There was no signature entry corresponding to this tag. Fail.
          return MatchResult.UNEXPECTED_ARGUMENT;
        }
      }
      Entry entry = entries.get(entryIndex);
      if (paramsSeen.get(entry.index)) {
        // We've seen this entry before; fail.
        return MatchResult.REDUNDANT_ARGUMENT;
      }
      int score = entry.guard.match(value, hierarchy);
      if (!Guard.isMatch(score)) {
        return MatchResult.GUARD_REJECTED;
      } else {
        paramsSeen.set(entry.index);
        scores[i] = score;
        if (!entry.isOptional)
          mandatoryArgsSeenCount++;
      }
    }
    if (mandatoryArgsSeenCount < mandatoryParamCount) {
      // There are some parameters we haven't seen. Fail.
      return MatchResult.MISSING_ARGUMENT;
    } else {
      // We saw all parameters and their guards approved. Match!
      return onMatch;
    }
  }

  @Override
  public String toString() {
    return Objects.toString(this.entries);
  }

}
