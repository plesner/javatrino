package org.ne.utrino.interpreter;

import java.util.Stack;

import org.ne.utrino.ast.Invocation.RInvocationDescriptor;
import org.ne.utrino.runtime.IInvocation;
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
    return run(bottom);
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
   * Continues executing at the given activation.
   */
  public static IValue run(Activation current) {
    int[] code = current.getBlock().getCode();
    IValue[] constants = current.getBlock().getConstants();
    int pc = current.getPc();
    while (true) {
      switch (code[pc]) {
        case Opcode.kPush: {
          IValue value = constants[code[pc + 1]];
          current.getStack().push(value);
          pc += 2;
          continue;
        }
        case Opcode.kInvoke: {
          // Resolve the method to invoke.
          RInvocationDescriptor desc = (RInvocationDescriptor) constants[code[pc + 1]];
          Invocation invoke = new Invocation(desc, current.getStack());
          RContext context = current.getBlock().getContext();
          RMethod method = context.getMethodSpace().lookupMethod(invoke);
          Assert.notNull(method);
          // Push an activation.
          current.setPc(pc + 2);
          current = new Activation(current, desc.getOrder().length, method.getCode());
          code = current.getBlock().getCode();
          constants = current.getBlock().getConstants();
          pc = current.getPc();
          continue;
        }
        case Opcode.kNative: {
          RNativeMethod method = (RNativeMethod) constants[code[pc + 1]];
          IValue value = method.invoke(current, null);
          current.getStack().push(value);
          // fallthrough
        }
        case Opcode.kImplicitReturn: {
          IValue value = current.getStack().peek();
          Activation prevFrame = current;
          current = current.getBelow();
          if (current == null)
            return value;
          code = current.getBlock().getCode();
          constants = current.getBlock().getConstants();
          pc = current.getPc();
          for (int i = 0; i < prevFrame.getArgumentCount(); i++)
            current.getStack().pop();
          current.getStack().push(value);
          continue;
        }
        default: {
          throw new RuntimeException("Unknown opcode " + code[pc]);
        }
      }
    }
  }

}
