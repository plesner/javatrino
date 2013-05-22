package org.ne.utrino.ast;

import java.util.List;
import java.util.Objects;

import org.ne.utrino.runtime.Guard;
import org.ne.utrino.runtime.Signature;
import org.ne.utrino.util.Name;
import org.ne.utrino.value.RKey;
import org.ne.utrino.value.RString;
/**
 * The header of a method declarations.
 */
public class MethodHeader {

  private final String name;
  private final List<Parameter> params;

  public MethodHeader(String name, List<Parameter> params) {
    this.name = name;
    this.params = params;
  }

  /**
   * A single method parameter.
   */
  public static class Parameter {

    private final Name name;

    public Parameter(Name name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return Objects.toString(name);
    }

  }

  public Signature toSignature() {
    Signature.Builder builder = Signature.newBuilder();
    builder.addParameter(Guard.any()).addTag(RKey.THIS);
    builder.addParameter(Guard.identity(RString.of(this.name))).addTag(RKey.NAME);
    for (Parameter param : params)
      ;
    return builder.build();
  }

  @Override
  public String toString() {
    return name + params;
  }

}
