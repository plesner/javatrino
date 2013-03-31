package org.ne.utrino.plankton;

import java.io.ByteArrayInputStream;
/**
 * A binary input stream backed by a raw byte array.
 */
public class RawBinaryInputStream extends AbstractBinaryInputStream {

  private final ByteArrayInputStream data;
  
  public RawBinaryInputStream(byte[] data) {
    this.data = new ByteArrayInputStream(data);
  }
  
  @Override
  public int nextByte() throws DecodingError {
    return data.read();
  }

}
