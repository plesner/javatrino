package org.ne.utrino.plankton;
/**
 * Abstract implementation of the binary input stream interface.
 */
public abstract class AbstractBinaryInputStream implements IBinaryInputStream {

  @Override
  public int nextInt32() throws DecodingError {
    int result = 0;
    int offset = 0;
    int current;
    do {
      current = nextByte();
      result |= (current & 0x7F) << offset;
      offset += 7;
    } while ((current & 0x80) != 0);
    return result;
  }

  private static final byte[] EMPTY_BYTES = new byte[0];
  @Override
  public byte[] nextBlob() throws DecodingError {
    int bytes = nextInt32();
    if (bytes == 0)
      return EMPTY_BYTES;
    byte[] result = new byte[bytes];
    for (int i = 0; i < bytes; i++)
      result[i] = (byte) nextByte();
    return result;
  }
  
}
