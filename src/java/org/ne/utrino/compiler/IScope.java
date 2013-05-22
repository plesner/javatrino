package org.ne.utrino.compiler;

import org.ne.utrino.util.Name;

/**
 * Interface for a scope.
 */
public interface IScope {

  /**
   * Returns the symbol bound to the given name and notify any relevant intermediate
   * scopes that this name is being read from an inner scope.
   */
  public ISymbol readName(Name name);

}
