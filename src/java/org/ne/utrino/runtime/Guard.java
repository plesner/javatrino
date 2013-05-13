package org.ne.utrino.runtime;

import org.ne.utrino.value.IValue;

/**
 * A guard on an entry in a method pattern.
 */
public abstract class Guard {

  /**
   * An identity guard that matches perfectly when values are identical to this
   * guard's value and not at all when they're not.
   */
  private static class Identity extends Guard {

    private final IValue value;

    public Identity(IValue value) {
      this.value = value;
    }

    @Override
    public int match(IValue input) {
      return this.value.isIdentical(input) ? PERFECT_MATCH : NO_MATCH;
    }

  }

  /**
   * A guard that accepts any value but scores them such that any other match
   * is considered better.
   */
  private static class Any extends Guard {

    @Override
    public int match(IValue input) {
      return WEAK_MATCH;
    }

    /**
     * Singleton instance.
     */
    private static final Any INSTANCE = new Any();

  }

  /**
   * Returns a guard that matches objects that are identical to the given value.
   */
  public static Guard identity(IValue value) {
    return new Identity(value);
  }

  /**
   * Returns a guard that matches any value.
   */
  public static Guard any() {
    return Any.INSTANCE;
  }

  /**
   * Returns true iff the given score signifies a match.
   */
  public static boolean isMatch(int score) {
    return score != NO_MATCH;
  }

  /**
   * Returns the score of matching this guard to the given value. The returned
   * score value can be inspected using the static helper methods on this class.
   */
  public abstract int match(IValue input);

  /**
   * Score that signifies that a guard didn't match at all.
   */
  public static final int NO_MATCH = Integer.MAX_VALUE;

  /**
   * The guard matches even though there are no arguments because it guards an
   * optional argument.
   */
  public static final int OPTIONAL_MATCH = NO_MATCH - 1;

  /**
   * Score that signifies that there is a match but any other, more specific,
   * match will be considered better.
   */
  private static final int WEAK_MATCH = NO_MATCH - 2;

  /**
   * This guard matched perfectly.
   */
  private static final int PERFECT_MATCH = 0;

}
