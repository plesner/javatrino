package org.ne.utrino.ast;

import org.ne.utrino.util.Name;

public class Dynamic implements ISymbol {

  private final Name name;

  public Dynamic(Name name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "$" + name.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (!(obj instanceof Dynamic)) {
      return false;
    } else {
      return this.name.equals(((Dynamic) obj).name);
    }
  }

}
