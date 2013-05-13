package org.ne.utrino.util;

import java.util.Comparator;

/**
 * A simple pair type.
 */
public class Pair<S, T> {

  private final S first;
  private final T second;

  private Pair(S first, T second) {
    this.first = first;
    this.second = second;
  }

  public S getFirst() {
    return first;
  }

  public T getSecond() {
    return second;
  }

  public static final <S, T> Pair<S, T> of(S first, T second) {
    return new Pair<S, T>(first, second);
  }

  @Override
  public String toString() {
    return "(" + first + ", " + second + ")";
  }

  @Override
  public int hashCode() {
    return first.hashCode() ^ second.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof Pair<?, ?>)) {
      return false;
    } else {
      Pair<?, ?> that = (Pair<?, ?>) obj;
      return getFirst().equals(that.getFirst())
          && getSecond().equals(that.getSecond());
    }
  }

  /**
   * Comparator that works for any pair whose first value is comparable to itself.
   */
  private static final Comparator<?> COMPARATOR = new Comparator<Pair<Comparable<Object>, Object>>() {
    @Override
    public int compare(Pair<Comparable<Object>, Object> arg0,
        Pair<Comparable<Object>, Object> arg1) {
      return arg0.getFirst().compareTo(arg1.getFirst());
    }
  };

  /**
   * Returns a comparator that compares
   */
  @SuppressWarnings("unchecked")
  public static <S extends Comparable<S>, T> Comparator<Pair<S, T>> firstComparator() {
    return (Comparator<Pair<S, T>>) COMPARATOR;
  }

}
