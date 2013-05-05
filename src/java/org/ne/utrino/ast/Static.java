package org.ne.utrino.ast;

import org.ne.utrino.util.Name;

public class Static implements ISymbol {

  private final Name name;

  public Static(Name name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "@" + name.toString();
  }

  @Override
  public Era getEra() {
    return Era.PAST;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (!(obj instanceof Static)) {
      return false;
    } else {
      return this.name.equals(((Static) obj).name);
    }
  }

}
