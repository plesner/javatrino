package org.ne.utrino.ast;

import java.util.List;

public class Unit {

  private final List<IDeclaration> decls;

  public Unit(List<IDeclaration> decls) {
    this.decls = decls;
  }

}
