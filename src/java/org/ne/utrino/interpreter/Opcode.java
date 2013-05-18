package org.ne.utrino.interpreter;

/**
 * Static container for opcode constants.
 */
public enum Opcode {

  PUSH(0),
  INVOKE(1),
  IMPLICIT_RETURN(2),
  NATIVE(3);

  public static final int kPush = 0;
  public static final int kInvoke = 1;
  public static final int kImplicitReturn = 2;
  public static final int kNative = 3;

  private final int value;

  private Opcode(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

}
