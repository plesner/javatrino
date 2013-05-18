package org.ne.utrino.ast;

import java.util.Collections;
import java.util.List;

import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.interpreter.Opcode;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Pair;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.RInternalData;
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

  @SafeVarargs
  public Invocation(Pair<? extends ITagValue, ? extends IExpression>... args) {
    List<Pair<ITagValue, Integer>> tagOrder = Factory.newArrayList();
    this.tags = new ITagValue[args.length];
    this.values = new IExpression[args.length];
    for (int i = 0; i < args.length; i++) {
      Pair<? extends ITagValue, ? extends IExpression> arg = args[i];
      tagOrder.add(Pair.<ITagValue, Integer>of(arg.getFirst(), i));
      this.tags[i] = arg.getFirst();
      this.values[i] = arg.getSecond();
    }
    Collections.sort(tagOrder, Pair.<ITagValue, Integer>firstComparator());
    this.order = new int[args.length];
    for (int i = 0; i < args.length; i++) {
      order[i] = tagOrder.get(i).getSecond();
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
     * Returns the tag vector in evaluation order.
     */
    public ITagValue[] getTags() {
      return this.tags;
    }

  }

  @Override
  public void emit(Assembler assm) {
    for (IExpression arg : values)
      arg.emit(assm);
    RInvocationDescriptor desc = new RInvocationDescriptor(order, tags);
    int descIndex = assm.registerConstant(desc);
    assm.write(Opcode.INVOKE, descIndex);
  }

}
