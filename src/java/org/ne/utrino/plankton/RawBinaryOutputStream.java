package org.ne.utrino.plankton;

import java.io.ByteArrayOutputStream;
/**
 * A binary output stream backed by a raw array of bytes.
 */
public class RawBinaryOutputStream extends AbstractBinaryOutputStream {
  
  private final ByteArrayOutputStream data = new ByteArrayOutputStream();
  
  @Override
  protected void ensureCapacityDelta(int delta) {
    // ignore
  }

  @Override
  protected void append(int value) {
    data.write(value);
  }
  
  public byte[] toByteArray() {
    return data.toByteArray();
  }

}
