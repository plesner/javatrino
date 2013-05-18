package org.ne.utrino.interpreter;

import java.util.List;
import java.util.Map;

import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.Phase;
import org.ne.utrino.value.RContext;

/**
 * Assembler for building syntax trees into bytecode.
 */
public class Assembler {

  private final RContext context;
  private final List<Integer> instrs = Factory.newArrayList();
  private final Map<IValue, Integer> constantMap = Factory.newHashMap();
  private final List<IValue> constants = Factory.newArrayList();

  public Assembler(RContext context) {
    this.context = context;
  }

  /**
   * Writes an instruction to the instruction stream.
   */
  public void write(Opcode code, int... args) {
    instrs.add(code.getValue());
    for (int arg : args)
      instrs.add(arg);
  }

  /**
   * Writes the postscript that ends this code.
   */
  public void close() {
    write(Opcode.IMPLICIT_RETURN);
  }

  /**
   * Registers a value as a constant that can be accessed from opcodes.
   */
  public int registerConstant(IValue value) {
    Assert.equals(Phase.DEEP_IMMUTABLE, value.getPhase());
    Integer oldIndex = constantMap.get(value);
    if (oldIndex != null)
      return oldIndex;
    int nextIndex = constants.size();
    constants.add(value);
    constantMap.put(value, nextIndex);
    return nextIndex;
  }

  /**
   * Returns a code block containing the code generated through this assembler.
   */
  public CodeBlock toCodeBlock() {
    int[] code = new int[instrs.size()];
    for (int i = 0; i < instrs.size(); i++)
      code[i] = instrs.get(i);
    IValue[] constants = this.constants.toArray(new IValue[this.constants.size()]);
    return new CodeBlock(context, code, constants);
  }

}
