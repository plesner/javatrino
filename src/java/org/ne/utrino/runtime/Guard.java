package org.ne.utrino.runtime;

import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RProtocol;
import org.ne.utrino.value.Species;

/**
 * A guard on an entry in a method pattern. Matching against a guard yields an
 * integer which shouldn't be used directly but only with the utility methods
 * defined in Guard. As a general principle though, lower values signify better
 * matches.
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
    public int match(IValue input, IHierarchy hierarchy) {
      return this.value.isIdentical(input) ? IDENTICAL_MATCH : NO_MATCH;
    }

    @Override
    public String toString() {
      return "eq(" + value + ")";
    }

  }

  private static class Is extends Guard {

    private final RProtocol protocol;

    public Is(RProtocol protocol) {
      this.protocol = protocol;
    }

    @Override
    public String toString() {
      return "is(" + protocol + ")";
    }

    @Override
    public int match(IValue input, IHierarchy hierarchy) {
      Species species = input.getSpecies();
      return findBestMatch(species.getPrimary(), PERFECT_IS_MATCH, hierarchy);
    }

    /**
     * Finds the best match of the given protocol in the given hierarchy. If a
     * match is found the distance from here will be added to the given part
     * distance and returned as the score, otherwise NO_MATCH will be returned.
     */
    private int findBestMatch(RProtocol current, int dist, IHierarchy hierarchy) {
      if (protocol.isIdentical(current)) {
        return dist;
      } else {
        int score = NO_MATCH;
        for (RProtocol parent : hierarchy.getParents(current)) {
          int nextScore = findBestMatch(parent, dist + 1, hierarchy);
          score = bestScore(nextScore, score);
        }
        return score;
      }
    }

  }

  /**
   * A guard that accepts any value but scores them such that any other match
   * is considered better.
   */
  private static class Any extends Guard {

    @Override
    public int match(IValue input, IHierarchy hierarchy) {
      return WEAK_MATCH;
    }

    @Override
    public String toString() {
      return "*";
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
   * Returns a guard that scores objects according to how specifically they
   * belong to the given protocol.
   */
  public static Guard is(RProtocol protocol) {
    return new Is(protocol);
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
   * Returns the closest match of a and b.
   */
  public static int bestScore(int a, int b) {
    return Math.min(a, b);
  }

  /**
   * Compares which score is best. If a is better than b then it will compare
   * smaller, equally good compares equal, and worse compares greater than.
   */
  public static int compareScore(int a, int b) {
    return Integer.compare(a, b);
  }

  /**
   * Returns the score of matching this guard to the given value. The returned
   * score value can be inspected using the static helper methods on this class.
   */
  public abstract int match(IValue input, IHierarchy hierarchy);

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
  private static final int IDENTICAL_MATCH = 0;

  /**
   * It's not an identical match but the closest possible instanceof-match.
   */
  private static final int PERFECT_IS_MATCH = IDENTICAL_MATCH + 1;

}
