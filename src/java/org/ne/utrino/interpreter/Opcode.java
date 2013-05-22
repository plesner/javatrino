package org.ne.utrino.interpreter;

/**
 * Static container for opcode constants.
 */
public enum Opcode {

  PUSH(0),
  INVOKE(1),
  IMPLICIT_RETURN(2),
  NATIVE(3),
  CONTROL(4),
  POP_BELOW(5),
  LOCAL(6),
  ARGUMENT(7);

  public static final int kPush = 0;
  public static final int kInvoke = 1;
  public static final int kImplicitReturn = 2;
  public static final int kNative = 3;
  public static final int kControl = 4;
  public static final int kPopIntermediate = 5;
  public static final int kLocal = 6;
  public static final int kArgument = 7;

  private final int value;

  private Opcode(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

}
