package org.ne.utrino.compiler;

import org.ne.utrino.ast.LocalDeclaration;

/**
 * A variable symbol.
 */
public class LocalSymbol implements ISymbol {

  private final LocalDeclaration origin;

  public LocalSymbol(LocalDeclaration origin) {
    this.origin = origin;
  }

}
