package org.ne.utrino.interpreter;

import java.util.Stack;

import org.ne.utrino.ast.Invocation.RInvocationDescriptor;
import org.ne.utrino.util.Factory;
import org.ne.utrino.value.IValue;

/**
 * A single activation.
 */
public class Activation {

  private final Activation below;
  private final RInvocationDescriptor descriptor;
  private final CodeBlock block;
  private int pc = 0;
  private final Stack<IValue> stack = Factory.newStack();

  public Activation(Activation below, RInvocationDescriptor descriptor, CodeBlock block) {
    this.below = below;
    this.descriptor = descriptor;
    this.block = block;
  }

  /**
   * Returns the code being executed in this activation.
   */
  public CodeBlock getBlock() {
    return this.block;
  }

  /**
   * Returns the current program counter value.
   */
  public int getPc() {
    return this.pc;
  }

  /**
   * Sets the program counter to the given value.
   */
  public void setPc(int value) {
    this.pc = value;
  }

  /**
   * Returns this activation's stack.
   */
  public Stack<IValue> getStack() {
    return this.stack;
  }

  /**
   * Returns the activation that called this one.
   */
  public Activation getBelow() {
    return this.below;
  }

  /**
   * Returns the number of arguments passed to this invocation.
   */
  public int getArgumentCount() {
    return this.descriptor.getArgumentCount();
  }

  /**
   * Returns the index'th argument to this call.
   */
  public IValue getArgument(int index) {
    Stack<IValue> outer = below.getStack();
    return outer.get(outer.size() - this.descriptor.getArgumentCount() + index);
  }

}
