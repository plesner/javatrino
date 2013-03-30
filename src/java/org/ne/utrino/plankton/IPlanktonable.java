package org.ne.utrino.plankton;
/**
 * Interface for types that can convert themselves to structured
 * plankton wrapper objects.
 */
public interface IPlanktonable<P extends IPlanktonObject> {

  public P toPlankton();

}
