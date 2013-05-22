package org.ne.utrino.util;

import java.util.Arrays;
import java.util.List;

import org.ne.utrino.value.ITagValue;

public class Name {

  private final List<ITagValue> parts;

  public Name(List<ITagValue> parts) {
    this.parts = parts;
  }

  public static Name of(ITagValue... parts) {
    return new Name(Arrays.asList(parts));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (!(obj instanceof Name)) {
      return false;
    } else {
      return this.parts.equals(((Name) obj).parts);
    }
  }

  @Override
  public String toString() {
    return parts.toString();
  }

}
