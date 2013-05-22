package org.ne.utrino.compiler;

import org.ne.utrino.ast.MethodHeader.Parameter;
import org.ne.utrino.interpreter.Assembler;

public class ParameterSymbol implements ISymbol {

  private final Parameter param;

  public ParameterSymbol(Parameter param) {
    this.param = param;
  }

  @Override
  public void read(Assembler assm) {
    assm.readArgument(param.getTag());
  }

}
