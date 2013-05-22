package org.ne.utrino.interpreter;

import java.util.Stack;

import org.ne.utrino.ast.Invocation.RInvocationDescriptor;
import org.ne.utrino.runtime.IInvocation;
import org.ne.utrino.runtime.RControlMethod;
import org.ne.utrino.runtime.RNativeMethod;
import org.ne.utrino.util.Assert;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RContext;
import org.ne.utrino.value.RMethod;

/**
 * Simple bytecode interpreter.
 */
public class Interpreter {

  /**
   * Executes the given code block, returning the resulting value.
   */
  public static IValue interpret(CodeBlock block) {
    Activation bottom = new Activation(null, 0, block);
    return new Interpreter().run(bottom);
  }

  /**
   * A runtime invocation descriptor used for method lookup.
   */
  private static class Invocation implements IInvocation {

    private final RInvocationDescriptor desc;
    private final Stack<IValue> stack;

    public Invocation(RInvocationDescriptor desc, Stack<IValue> stack) {
      this.desc = desc;
      this.stack = stack;
    }

    @Override
    public int getEntryCount() {
      return desc.getOrder().length;
    }

    @Override
    public ITagValue getTag(int index) {
      return desc.getTags()[desc.getOrder()[index]];
    }

    @Override
    public IValue getValue(int index) {
      int offset = stack.size() - getEntryCount();
      return stack.get(offset + desc.getOrder()[index]);
    }

  }

  /**
   * Returns the invocation descriptor for the invocation this frame is blocked
   * on.
   */
  public static RInvocationDescriptor getDescriptor(Activation frame) {
    int index = frame.getBlock().getCode()[frame.getPc() - 1];
    return (RInvocationDescriptor) frame.getBlock().getConstants()[index];
  }

  /**
   * Returns a descriptor for the invocation this activation is blocked after.
   */
  public static Invocation getInvocation(Activation frame) {
    RInvocationDescriptor desc = getDescriptor(frame);
    return new Invocation(desc, frame.getStack());
  }

  private Activation frame;
  private int[] code;
  private IValue[] constants;
  private int pc;
  private Stack<IValue> stack;

  /**
   * Sets the given frame as the current one. This can be used both when entering
   * and exiting frames (which becomes entering return frames).
   */
  public void enterActivation(Activation frame) {
    this.frame = frame;
    this.code = frame.getBlock().getCode();
    this.constants = frame.getBlock().getConstants();
    this.pc = frame.getPc();
    this.stack = frame.getStack();
  }

  /**
   * Returns a new activation that executes a call to the given method, returning
   * to the given frame when complete.
   */
  public static Activation setUpCall(Activation frame, RMethod method) {
    RInvocationDescriptor desc = getDescriptor(frame);
    return new Activation(frame, desc.getOrder().length, method.getCode());
  }

  /**
   * Continues executing at the given activation.
   */
  private IValue run(Activation start) {
    this.enterActivation(start);
    while (true) {
      switch (code[pc]) {
        case Opcode.kPush: {
          IValue value = constants[code[pc + 1]];
          stack.push(value);
          pc += 2;
          continue;
        }
        case Opcode.kInvoke: {
          // Block the current activation behind this invocation.
          frame.setPc(pc + 2);
          // Resolve the method to invoke.
          Invocation invoke = getInvocation(frame);
          RContext context = frame.getBlock().getContext();
          RMethod method = context.getMethodSpace().lookupMethod(invoke);
          Assert.notNull(method);
          // Push an activation.
          Activation nextFrame = setUpCall(frame, method);
          enterActivation(nextFrame);
          continue;
        }
        case Opcode.kNative: {
          RNativeMethod method = (RNativeMethod) constants[code[pc + 1]];
          IValue value = method.invoke(frame, null);
          stack.push(value);
          // fallthrough
        }
        case Opcode.kImplicitReturn: {
          IValue value = stack.peek();
          Activation above = frame;
          Activation below = frame.getBelow();
          if (below == null)
            return value;
          enterActivation(below);
          for (int i = 0; i < above.getArgumentCount(); i++)
            stack.pop();
          stack.push(value);
          continue;
        }
        case Opcode.kControl: {
          RControlMethod method = (RControlMethod) constants[code[pc + 1]];
          pc += 2;
          method.invoke(frame, this);
          break;
        }
        default: {
          throw new RuntimeException("Unknown opcode " + code[pc]);
        }
      }
    }
  }

}
