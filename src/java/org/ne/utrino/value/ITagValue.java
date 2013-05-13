package org.ne.utrino.value;
/**
 * A value that can be used as a method argument tag. Tag values must be
 * transitively immutable.
 */
public interface ITagValue extends IValue, Comparable<ITagValue> {

  /**
   * The class or flavor of this value. This is used to generate a predictable
   * ordering between different types of tag values.
   */
  public enum Flavor {
    INTEGER,
    STRING
  }

  /**
   * Returns this tag's flavor.
   */
  public Flavor getFlavor();

}
