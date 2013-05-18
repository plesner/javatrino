package org.ne.utrino.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.ne.utrino.interpreter.Activation;
import org.ne.utrino.interpreter.Assembler;
import org.ne.utrino.interpreter.CodeBlock;
import org.ne.utrino.interpreter.Opcode;
import org.ne.utrino.util.Exceptions;
import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;
import org.ne.utrino.value.RBool;
import org.ne.utrino.value.RContext;
import org.ne.utrino.value.RDeepImmutable;
import org.ne.utrino.value.RInteger;
import org.ne.utrino.value.RKey;
import org.ne.utrino.value.RMethod;
import org.ne.utrino.value.RObject;
import org.ne.utrino.value.RProtocol;
import org.ne.utrino.value.RString;
import org.ne.utrino.value.Species;

public class NativeMethods {

  /* ---
   * O b j e c t
   * --- */

  @Native(self=RObject.class, name="==", first=RObject.class)
  private static final RNativeMethod OBJ_EQ = new RNativeMethod() {
    @Override
    public IValue invoke(Activation activation, InlineInfo inlineInfo) {
      boolean result = activation.getArgument(0).isIdentical(activation.getArgument(2));
      return RBool.of(result);
    }
  };


  /* ---
   * I n t e g e r
   * --- */

  @Native(self=RInteger.class, name="+", first=RInteger.class)
  private static final RNativeMethod INT_PLUS = new RNativeMethod() {
    @Override
    public IValue invoke(Activation activation, InlineInfo inlineInfo) {
      int a = ((RInteger) activation.getArgument(0)).getValue();
      int b = ((RInteger) activation.getArgument(2)).getValue();
      return new RInteger(a + b);
    }
  };

  @Native(self=RInteger.class, name="-", first=RInteger.class)
  private static final RNativeMethod INT_MINUS = new RNativeMethod() {
    @Override
    public IValue invoke(Activation activation, InlineInfo inlineInfo) {
      int a = ((RInteger) activation.getArgument(0)).getValue();
      int b = ((RInteger) activation.getArgument(2)).getValue();
      return new RInteger(a - b);
    }
  };

  /**
   * Marker for a native method field that gives the default signature for that
   * native.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface Native {
    public String name();
    public Class<? extends IValue> self();
    public Class<? extends IValue> first() default RNoneMarker.class;
  }

  /**
   * Dummy marker class that signifies that no type has been specified.
   */
  private static class RNoneMarker extends RDeepImmutable {
    @Override
    public Species getSpecies() {
      return null;
    }
    @Override
    public boolean objectEquals(IValue obj) {
      return false;
    }
  }

  /**
   * Adds all the native methods defined in this class to the given context.
   */
  public static void addToContext(RContext context) {
    MethodSpace methodSpace = context.getMethodSpace();
    // Set up inheritance.
    methodSpace.addInheritance(RInteger.getProtocol(), RObject.getProtocol());
    methodSpace.addInheritance(RBool.getProtocol(), RObject.getProtocol());
    // Set up methods.
    for (Field field : NativeMethods.class.getDeclaredFields()) {
      Native marker = field.getAnnotation(Native.class);
      if (marker == null)
        continue;
      CodeBlock code = getNativeMethodCode(field);
      Signature signature = buildSignature(marker);
      RMethod method = new RMethod(signature, code);
      methodSpace.addMethod(method);
    }
  }

  /**
   * Returns the code block that calls the native method stored in the given
   * field.
   */
  private static CodeBlock getNativeMethodCode(Field field) {
    RNativeMethod method = getNativeMethod(field);
    Assembler assm = new Assembler(null);
    int index = assm.registerConstant(method);
    assm.write(Opcode.NATIVE, index);
    return assm.toCodeBlock();
  }

  /**
   * Extracts the native method value of the given field.
   */
  private static RNativeMethod getNativeMethod(Field field) {
    try {
      return (RNativeMethod) field.get(null);
    } catch (IllegalArgumentException iae) {
      throw Exceptions.propagate(iae);
    } catch (IllegalAccessException iae) {
      throw Exceptions.propagate(iae);
    }
  }

  /**
   * Builds a method signature from a native method marker annotation.
   */
  private static Signature buildSignature(Native marker) {
    Signature.Builder builder = Signature.newBuilder();
    builder
        .addParameter(Guard.identity(RString.of(marker.name())))
        .addTag(RKey.NAME);
    addParameter(builder, RKey.THIS, marker.self());
    addParameter(builder, RInteger.of(0), marker.first());
    return builder.build();
  }

  /**
   * Adds a parameter to the given signature with the given tag that matches
   * instances of the given class.
   */
  private static void addParameter(Signature.Builder builder, ITagValue tag,
      Class<? extends IValue> klass) {
    if (klass == RNoneMarker.class)
      return;
    RProtocol protocol = getProtocol(klass);
    builder
        .addParameter(Guard.is(protocol))
        .addTag(tag);
  }

  /**
   * Extracts the protocol object for the given class.
   */
  private static RProtocol getProtocol(Class<? extends IValue> klass) {
    try {
      return (RProtocol) klass.getMethod("getProtocol").invoke(null);
    } catch (IllegalAccessException iae) {
      throw Exceptions.propagate(iae);
    } catch (IllegalArgumentException iae) {
      throw Exceptions.propagate(iae);
    } catch (InvocationTargetException ite) {
      throw Exceptions.propagate(ite);
    } catch (NoSuchMethodException nsme) {
      throw Exceptions.propagate(nsme);
    } catch (SecurityException se) {
      throw Exceptions.propagate(se);
    }
  }

}
