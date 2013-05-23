package org.ne.utrino.ast;

import java.util.List;

import org.ne.utrino.compiler.ISymbol;
import org.ne.utrino.compiler.ParameterSymbol;
import org.ne.utrino.runtime.Guard;
import org.ne.utrino.runtime.Signature;
import org.ne.utrino.runtime.Signature.ParameterBuilder;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Name;
import org.ne.utrino.value.ITagValue;
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
    private final List<ITagValue> tags;
    private final ISymbol symbol = new ParameterSymbol(this);

    public Parameter(Name name, List<ITagValue> tags) {
      Assert.that(!tags.isEmpty());
      this.name = name;
      this.tags = tags;
    }

    @Override
    public String toString() {
      return tags + ": " + name;
    }

    /**
     * Returns the symbol that represents this parameter.
     */
    public ISymbol getSymbol() {
      return this.symbol;
    }

    /**
     * Returns this parameter's name.
     */
    public Name getName() {
      return name;
    }

    /**
     * Returns this parameter's tags.
     */
    public List<ITagValue> getTags() {
      return this.tags;
    }

  }

  /**
   * Builds this methods's signature.
   */
  public Signature toSignature() {
    Signature.Builder builder = Signature.newBuilder();
    builder.addParameter(Guard.any()).addTag(RKey.THIS);
    builder.addParameter(Guard.identity(RString.of(this.name))).addTag(RKey.NAME);
    for (Parameter param : params) {
      ParameterBuilder paramBuilder = builder.addParameter(Guard.any());
      for (ITagValue tag : param.getTags())
        paramBuilder.addTag(tag);
    }
    return builder.build();
  }

  /**
   * Returns the list of parameters.
   */
  public List<Parameter> getParameters() {
    return this.params;
  }

  @Override
  public String toString() {
    return name + params;
  }

}
