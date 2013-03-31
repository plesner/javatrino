package org.ne.utrino.plankton;


/**
 * Types that implement this interface are asked to convert themselves
 * to plankton when being serialized.
 */
public interface IPlanktonDatable {

  /**
   * Return a plankton representation of this object.
   */
  public ISeed toPlanktonData(IPlanktonFactory factory);

}
