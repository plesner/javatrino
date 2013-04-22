package org.ne.utrino.ast;

import org.ne.utrino.util.Name;

public class Static implements ISymbol {

  private final Name name;

  public Static(Name name) {
    this.name = name;
  }

}
