package org.ne.utrino.util;

/**
 * A simple filter that turns objects of type F into type T.
 */
public interface IFunction<F, T> {

  public T call(F arg);

}
