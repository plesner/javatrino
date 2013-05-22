package org.ne.utrino.ast;

import java.util.Collections;
import java.util.List;

import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Pair;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.RInteger;
import org.ne.utrino.value.RInternalData;
import org.ne.utrino.value.RKey;
import org.ne.utrino.value.RString;
/**
 * A multi-method invocation.
 */
public class Invocation implements IExpression {

  /**
   * The order to read tags to get them in sorted order.
   */
  private final int[] order;

  /**
   * Tags in evaluation order.
   */
  private final ITagValue[] tags;

  /**
   * Argument expressions in evaluation order.
   */
  private final IExpression[] values;

  public Invocation(List<? extends Pair<? extends ITagValue, ? extends IExpression>> args) {
    List<Pair<ITagValue, Integer>> tagOrder = Factory.newArrayList();
    this.tags = new ITagValue[args.size()];
    this.values = new IExpression[args.size()];
    for (int i = 0; i < args.size(); i++) {
      Pair<? extends ITagValue, ? extends IExpression> arg = args.get(i);
      tagOrder.add(Pair.<ITagValue, Integer>of(arg.getFirst(), i));
      this.tags[i] = arg.getFirst();
      this.values[i] = arg.getSecond();
    }
    Collections.sort(tagOrder, Pair.<ITagValue, Integer>firstComparator());
    this.order = new int[args.size()];
    for (int i = 0; i < args.size(); i++) {
      order[i] = tagOrder.get(i).getSecond();
    }
  }

  @Override
  public <T> void accept(IVisitor<T> visitor, T data) {
    visitor.visitInvocation(this, data);
  }

  @Override
  public void emit(Assembler assm) {
    for (IExpression arg : values)
      arg.emit(assm);
    RInvocationDescriptor desc = new RInvocationDescriptor(order, tags);
    assm.invoke(desc);
  }

  /**
   * Returns the argument expressions to this invocation.
   */
  public IExpression[] getArguments() {
    return this.values;
  }

  public static class RInvocationDescriptor extends RInternalData {

    private final int[] order;
    private final ITagValue[] tags;

    public RInvocationDescriptor(int[] order, ITagValue[] tags) {
      this.order = order;
      this.tags = tags;
    }

    /**
     * Returns the sort order of the tag values.
     */
    public int[] getOrder() {
      return this.order;
    }

    /**
     * Returns the number of arguments being passed.
     */
    public int getArgumentCount() {
      return this.order.length;
    }

    /**
     * Returns the tag vector in evaluation order.
     */
    public ITagValue[] getTags() {
      return this.tags;
    }

  }

  /**
   * A utility for consing up invocations.
   */
  public static class Builder {

    private final List<Pair<ITagValue, IExpression>> entries = Factory.newArrayList();

    public Invocation build() {
      return new Invocation(entries);
    }

    /**
     * Sets the receiver argument.
     */
    public Builder setThis(IExpression value) {
      entries.add(Pair.<ITagValue, IExpression>of(RKey.THIS, value));
      return this;
    }

    /**
     * Sets the method name argument.
     */
    public Builder setName(String name) {
      entries.add(Pair.<ITagValue, IExpression>of(RKey.NAME, new Literal(RString.of(name))));
      return this;
    }

    /**
     * Adds a positional argument.
     */
    public Builder setPositional(int index, IExpression value) {
      entries.add(Pair.<ITagValue, IExpression>of(RInteger.of(index), value));
      return this;
    }

  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("{");
    for (int i = 0; i < order.length; i++) {
      if (i > 0)
        buf.append(", ");
      buf.append(tags[i]).append(": ").append(values[i]);
    }
    buf.append("}");
    return buf.toString();
  }

}
