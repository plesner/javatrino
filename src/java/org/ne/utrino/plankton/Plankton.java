package org.ne.utrino.plankton;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.ne.utrino.plankton.IPlanktonFactory.IPlanktonArray;
import org.ne.utrino.plankton.IPlanktonFactory.IPlanktonMap;
import org.ne.utrino.plankton.IPlanktonFactory.IPlanktonSeed;
import org.ne.utrino.util.Assert;
import org.ne.utrino.util.Exceptions;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Internal;


/**
 * Container class for methods for encoding and decoding plankton.
 */
public class Plankton {

  private static final byte tInteger = 0;
  private static final byte tString = 1;
  private static final byte tList = 2;
  private static final byte tMap = 3;
  private static final byte tNull = 4;
  private static final byte tBool = 5;
  private static final byte tSeed = 6;
  private static final byte tRef = 7;

  private static class Encoder {

    private final IBinaryOutputStream out;
    private final IdentityHashMap<Object, Integer> seen = Factory.newIdentityHashMap();

    public Encoder(IBinaryOutputStream out) {
      this.out = out;
    }

    /**
     * Encodes an object as binary plankton, outputting on the given
     * stream.
     */
    public void encode(Object value) {
      if (value instanceof String) {
        out.addByte(tString);
        encodeString((String) value, out);
      } else if (value instanceof Integer) {
        out.addByte(tInteger).addInt32(((Integer) value).intValue());
      } else if (value instanceof Boolean) {
        out.addByte(tBool).addByte(value == Boolean.TRUE ? 1 : 0);
      } else if (value instanceof Map<?, ?>) {
        Map<?, ?> map = (Map<?, ?>) value;
        out.addByte(tMap).addInt32(map.size());
        for (Map.Entry<?, ?> entry : map.entrySet()) {
          encode(entry.getKey());
          encode(entry.getValue());
        }
      } else if (value instanceof Collection<?>) {
        Collection<?> coll = (Collection<?>) value;
        out.addByte(tList).addInt32(coll.size());
        for (Object elm : coll)
          encode(elm);
      } else if (value == null) {
        out.addByte(tNull);
      } else if (value instanceof ISeed || value instanceof IPlanktonDatable) {
        Integer index = seen.get(value);
        if (index == null) {
          index = seen.size();
          seen.put(value, index);
          ISeed seed;
          if (value instanceof ISeed) {
            seed = (ISeed) value;
          } else {
            seed = ((IPlanktonDatable) value).toPlanktonData(FACTORY);
          }
          out.addByte(tSeed);
          encode(seed.getHeader());
          encode(seed.getPayload());
        } else {
          out.addByte(tRef);
          out.addInt32(index);
        }
      } else {
        Assert.that(false);
      }
    }

  }

  public static void encode(Object value, IBinaryOutputStream out) {
    new Encoder(out).encode(value);
  }

  private static class Decoder {

    private final IBinaryInputStream in;
    private final ArrayList<ISeed> seedIndex = Factory.newArrayList();

    public Decoder(IBinaryInputStream in) {
      this.in = in;
    }

    /**
     * Reads a stream of binary plankton, returning the object represented.
     * Throws a {@link DecodingError} if the input is somehow invalid.
     */
    public Object decode() throws DecodingError {
      switch (in.nextByte()) {
      case tBool:
        return (in.nextByte() == 1) ? true : false;
      case tString:
        return decodeString(in);
      case tInteger:
        return in.nextInt32();
      case tMap: {
        int entries = in.nextInt32();
        Map<Object, Object> result = Factory.newHashMap();
        for (int i = 0; i < entries; i++) {
          Object key = decode();
          Object value = decode();
          result.put(key, value);
        }
        return result;
      }
      case tList: {
        int length = in.nextInt32();
        List<Object> result = Factory.newArrayList();
        for (int i = 0; i < length; i++)
          result.add(decode());
        return result;
      }
      case tNull:
        return null;
      case tSeed:
        PlanktonSeedImpl newSeed = new PlanktonSeedImpl();
        seedIndex.add(newSeed);
        newSeed.setHeader(decode());
        newSeed.setPayload(decode());
        return newSeed;
      case tRef:
        int index = in.nextInt32();
        return seedIndex.get(index);
      default:
        throw new DecodingError("Unexpected tag value");
      }
    }

  }

  public static Object decode(IBinaryInputStream in) throws DecodingError {
    return new Decoder(in).decode();
  }

  /**
   * Encodes a string as utf8 on the given stream.
   */
  private static void encodeString(String value, IBinaryOutputStream out) {
    byte[] bytes;
    try {
      bytes = value.getBytes("UTF-8");
    } catch (UnsupportedEncodingException uee) {
      throw Exceptions.propagate(uee);
    }
    out.addBlob(bytes);
  }

  /**
   * Reads a utf8 string from the given stream.
   */
  private static String decodeString(IBinaryInputStream in) throws DecodingError {
    byte[] bytes = in.nextBlob();
    try {
      return new String(bytes, "UTF-8");
    } catch (UnsupportedEncodingException uee) {
      throw Exceptions.propagate(uee);
    }
  }

  @Internal
  public static IPlanktonFactory getDefaultFactory() {
    return FACTORY;
  }

  private static final IPlanktonFactory FACTORY = new IPlanktonFactory() {
    @Override
    public IPlanktonMap newMap() {
      return new PlanktonMapImpl();
    }
    @Override
    public IPlanktonArray newArray() {
      return new PlanktonArrayImpl();
    }
    public IPlanktonSeed newSeed() {
      return new PlanktonSeedImpl();
    }
  };

  /**
   * A utility for consing up a json map.
   */
  @SuppressWarnings("serial")
  private static class PlanktonMapImpl extends HashMap<String, Object>
      implements IPlanktonMap {

    @Override
    public PlanktonMapImpl set(String key, Object value) {
      put(key, value);
      return this;
    }

  }

  /**
   * A utility for consing up a json array.
   */
  @SuppressWarnings("serial")
  private static class PlanktonArrayImpl extends ArrayList<Object>
      implements IPlanktonArray {

    @Override
    public PlanktonArrayImpl push(Object value) {
      this.add(value);
      return this;
    }

  }

  private static class PlanktonSeedImpl implements ISeed, IPlanktonSeed {

    private Object header;
    private Object payload;

    @Override
    public Object getHeader() {
      return this.header;
    }

    @Override
    public IPlanktonSeed setHeader(Object value) {
      this.header = value;
      return this;
    }

    @Override
    public Object getPayload() {
      return this.payload;
    }

    @Override
    public IPlanktonSeed setPayload(Object value) {
      this.payload = value;
      return this;
    }

  }

}
