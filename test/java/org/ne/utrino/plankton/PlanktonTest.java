package org.ne.utrino.plankton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class PlanktonTest extends TestCase {

  private Object transcode(Object value) throws DecodingError {
    RawBinaryOutputStream out = new RawBinaryOutputStream();
    Plankton.encode(value, out);
    byte[] data = out.toByteArray();
    RawBinaryInputStream in = new RawBinaryInputStream(data);
    return Plankton.decode(in);
  }

  private void checkCoding(Object value) throws DecodingError {
    assertEquals(value, transcode(value));
  }

  @Test
  public void testPrimitiveCoding() throws DecodingError {
    checkCoding(true);
    checkCoding(false);
    checkCoding(null);
    checkCoding(1);
    checkCoding(-1);
    checkCoding(0);
    checkCoding(1027);
    checkCoding(1027 * 1027);
    checkCoding(1027 * 1027 * 1027);
    checkCoding(-1027);
    checkCoding(-1027 * 1027);
    checkCoding(-1027 * 1027 * 1027);
    checkCoding("foobar");
    checkCoding("");
    checkCoding("foo\0bar");
    checkCoding("12-3401283094812309417249p583274o51893212");
  }

  @Test
  public void testCompositeCoding() throws DecodingError {
    checkCoding(Arrays.asList(1, 2, 3));
    checkCoding(Arrays.asList());
    checkCoding(Arrays.asList(1, "2", Arrays.asList(3, 4)));
    checkCoding(Arrays.asList(1, Arrays.asList(2, Arrays.asList(3,
        Arrays.asList(4, Arrays.asList(5, Arrays.asList(6)))))));
    checkCoding(Collections.emptyMap());
    checkCoding(Collections.singletonMap(1, "blah"));
    checkCoding(Plankton.getDefaultFactory().newMap()
        .set("a", 1)
        .set("b", 2)
        .set("c", 3));
  }

  private static class Pair implements IPlanktonDatable {

    private Object first;
    private Object second;

    public Pair(Object first, Object second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public ISeed toPlanktonData(IPlanktonFactory factory) {
      return factory
          .newSeed()
          .setHeader("Pair")
          .setPayload(factory
              .newArray()
              .push(first)
              .push(second));
    }

  }

  @Test
  public void testCycleCoding() throws DecodingError {
    Pair pair = new Pair(1, 2);
    pair.first = pair;
    ISeed seed = (ISeed) transcode(pair);
    List<?> payload = (List<?>) seed.getPayload();
    assertEquals(seed, payload.get(0));
    assertEquals(2, payload.get(1));
  }

  @Test
  public void testByteArrayStreams() throws DecodingError {
    RawBinaryOutputStream out = new RawBinaryOutputStream();
    out.addByte(1);
    out.addByte(8);
    out.addByte(127);
    out.addBlob(new byte[] {1, 8, 127});
    out.addInt32(6550892);
    out.addInt32(-6550892);
    out.addInt32(0);
    RawBinaryInputStream in = new RawBinaryInputStream(out.toByteArray());
    assertEquals(1, in.nextByte());
    assertEquals(8, in.nextByte());
    assertEquals(127, in.nextByte());
    byte[] blob = in.nextBlob();
    assertEquals(3, blob.length);
    assertEquals(1, blob[0]);
    assertEquals(8, blob[1]);
    assertEquals(127, blob[2]);
    assertEquals(6550892, in.nextInt32());
    assertEquals(-6550892, in.nextInt32());
    assertEquals(0, in.nextInt32());
  }

}
