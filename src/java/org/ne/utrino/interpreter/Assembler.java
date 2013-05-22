package org.ne.utrino.interpreter;

import java.util.List;
import java.util.Map;

import org.ne.utrino.ast.Invocation.RInvocationDescriptor;
import org.ne.utrino.compiler.ISymbol;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Factory;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RContext;

/**
 * Assembler for building syntax trees into bytecode.
 */
public class Assembler {

  private final RContext context;
  private final List<Integer> instrs = Factory.newArrayList();
  private final Map<IValue, Integer> constantMap = Factory.newHashMap();
  private final List<IValue> constants = Factory.newArrayList();
  private int stackHeight = 0;
  private final Map<ISymbol, Integer> stackMap = Factory.newHashMap();

  public Assembler(RContext context) {
    this.context = context;
  }

  /**
   * Returns the context in which this code will be run.
   */
  public RContext getContext() {
    return this.context;
  }

  /**
   * Returns the current stack height.
   */
  public int getStackHeight() {
    return this.stackHeight;
  }

  /**
   * Writes a push instruction.
   */
  public void push(IValue value) {
    int index = registerConstant(value);
    write(Opcode.PUSH, index);
    stackHeight++;
  }

  /**
   * Sets the name of the value on the top of the stack such that it can be
   * retrieved by an identifier later on.
   */
  public void setTopValueName(ISymbol symbol) {
    Assert.that(!this.stackMap.containsKey(symbol));
    this.stackMap.put(symbol, stackHeight - 1);
  }

  /**
   * Removes the given symbol as the name of a local.
   */
  public void forgetName(ISymbol symbol) {
    Assert.that(this.stackMap.containsKey(symbol));
    this.stackMap.remove(symbol);
  }

  /**
   * Returns the stack index of the given symbol.
   */
  public int getLocalIndex(ISymbol symbol) {
    Assert.that(this.stackMap.containsKey(symbol));
    return this.stackMap.get(symbol);
  }

  /**
   * Writes an invocation instruction.
   */
  public void invoke(RInvocationDescriptor desc) {
    int index = registerConstant(desc);
    write(Opcode.INVOKE, index);
    stackHeight -= (desc.getArgumentCount() - 1);
  }

  /**
   * Call a control primitive.
   */
  public void control(IValue handler) {
    int index = registerConstant(handler);
    write(Opcode.CONTROL, index);
  }

  /**
   * Call a native operation.
   */
  public void nathive(IValue handler) {
    int index = registerConstant(handler);
    write(Opcode.NATIVE, index);
  }

  /**
   * Pops the element below the top element, replacing it with the current top
   * element.
   */
  public void popBelow() {
    write(Opcode.POP_BELOW);
    stackHeight--;
  }

  /**
   * Reads the local at the given index.
   */
  public void local(int index) {
    write(Opcode.LOCAL, index);
    stackHeight++;
  }

  /**
   * Writes an instruction to the instruction stream.
   */
  private void write(Opcode code, int... args) {
    instrs.add(code.getValue());
    for (int arg : args)
      instrs.add(arg);
  }

  /**
   * Writes the postscript that ends this code.
   */
  public void close() {
    Assert.equals(1, stackHeight);
    write(Opcode.IMPLICIT_RETURN);
  }

  /**
   * Registers a value as a constant that can be accessed from opcodes.
   */
  public int registerConstant(IValue value) {
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
