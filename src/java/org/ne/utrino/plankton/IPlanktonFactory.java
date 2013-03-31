package org.ne.utrino.plankton;
/**
 * A factory for creating plankton objects. The server and client side use
 * different types objects to represent arrays and mapes and this
 * factory makes that possible.
 */
public interface IPlanktonFactory {

  /**
   * A plankton map builder.
   */
  public interface IPlanktonMap {

    public IPlanktonMap set(String key, Object value);

  }

  /**
   * A plankton array builder.
   */
  public interface IPlanktonArray {

    public IPlanktonArray push(Object value);

  }

  /**
   * A plankton seed builder.
   */
  public interface IPlanktonSeed extends ISeed {

    public IPlanktonSeed setHeader(Object value);

    public IPlanktonSeed setPayload(Object value);

  }

  /**
   * Creates a new empty map object.
   */
  public IPlanktonMap newMap();

  /**
   * Creates a new empty array object.
   */
  public IPlanktonArray newArray();

  /**
   * Creates a new empty seed object.
   */
  public IPlanktonSeed newSeed();

}
