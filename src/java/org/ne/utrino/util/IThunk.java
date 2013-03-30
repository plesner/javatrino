package org.ne.utrino.util;
/**
 * Like a function but without a return value.
 */
public interface IThunk<T> {

  public void call(T value);

}
