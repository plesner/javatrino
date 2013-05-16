package org.ne.utrino.runtime;

import java.util.Iterator;

import org.ne.utrino.util.Assert;
import org.ne.utrino.value.RMethod;

/**
 * An object that encapsulates state related to method lookup.
 */
public class MethodLookup {

  private final int maxArgCount;
  private final int[] bestScore;
  private final int[] scratchScore;
  private RMethod result;
  private boolean foundMatch;

  /**
   * Creates a new method lookup object that can look up methods for invocations
   * with up to the given number of arguments.
   */
  public MethodLookup(int maxArgCount) {
    this.maxArgCount = maxArgCount;
    this.bestScore = new int[maxArgCount];
    this.scratchScore = new int[maxArgCount];
  }

  /**
   * Returns the method found by the last lookup.
   */
  public RMethod getMethod() {
    return this.result;
  }

  /**
   * Was any matching method found?
   */
  public boolean foundMatch() {
    return this.foundMatch;
  }

  /**
   * Were matches found but no unique best?
   */
  public boolean isAmbiguous() {
    return this.foundMatch && (this.result == null);
  }

  /**
   * The outcome of joining two score vectors.
   */
  public enum JoinStatus {
    /**
     * The source was strictly better than the target.
     */
    BETTER,

    /**
     * The target was strictly better than the source.
     */
    WORSE,

    /**
     * The matches were equal.
     */
    EQUAL,

    /**
     * Neither was strictly better than the other, but they were different.
     */
    AMBIGUOUS;
  }

  /**
   * Finds the best matching method for the given invocation.
   */
  public void findMethod(IInvocation invocation, Iterable<RMethod> methods,
      IHierarchy hierarchy) {
    Assert.that(invocation.getEntryCount() <= maxArgCount);
    int argCount = invocation.getEntryCount();
    this.result = null;
    this.foundMatch = false;
    Iterator<RMethod> methodIter = methods.iterator();
    // First scan until we find the first match, using the best score vector
    // to hold the score directly.
    while (methodIter.hasNext()) {
      RMethod method = methodIter.next();
      Signature signature = method.getSignature();
      if (signature.match(invocation, hierarchy, bestScore).didMatch()) {
        this.result = method;
        this.foundMatch = true;
        break;
      }
    }
    // When comparing matches, do we require a new match to be strictly better
    // than the current one or is an equal one sufficient?
    boolean acceptEqual = false;
    // Then continue scanning but compare every new match to the existing best
    // score.
    while (methodIter.hasNext()) {
      RMethod method = methodIter.next();
      Signature signature = method.getSignature();
      if (!signature.match(invocation, hierarchy, scratchScore).didMatch())
        continue;
      JoinStatus status = join(bestScore, scratchScore, argCount);
      if (status == JoinStatus.BETTER || (acceptEqual && status == JoinStatus.EQUAL)) {
        // The next score better than the best we've seen so far so that makes
        // it unique.
        this.result = method;
        // We need unambiguous improvement to consider something better.
        acceptEqual = false;
      } else if (status != JoinStatus.WORSE) {
        // The next score was not strictly worse than the best we've seen so we
        // don't have a unique best.
        this.result = null;
        // If the methods we've seen now have been ambiguous then if we see a
        // method equal to the current best score that would make it strictly better
        // than any of the ambiguous ones we've seen so far.
        acceptEqual = (status == JoinStatus.AMBIGUOUS);
      }
    }
  }

  /**
   * Joins two score vectors together, writing the result into the target vector.
   * The returned value identifies what the outcome of the join was.
   */
  public static JoinStatus join(int[] target, int[] source, int length) {
    boolean targetBetter = false;
    boolean sourceBetter = false;
    for (int i = 0; i < length; i++) {
      int cmp = Guard.compareScore(target[i], source[i]);
      if (cmp < 0) {
        // The target was strictly better than the source.
        targetBetter = true;
      } else if (cmp > 0) {
        // The source was strictly better than the target; override.
        sourceBetter = true;
        target[i] = source[i];
      }
    }
    if (targetBetter) {
      return sourceBetter ? JoinStatus.AMBIGUOUS : JoinStatus.WORSE;
    } else {
      return sourceBetter ? JoinStatus.BETTER : JoinStatus.EQUAL;
    }
  }

  /**
   * What's the max number of arguments this lookup will handle?
   */
  public int getMaxArguments() {
    return this.maxArgCount;
  }

}
