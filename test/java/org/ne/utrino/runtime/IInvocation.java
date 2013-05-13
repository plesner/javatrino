package org.ne.utrino.runtime;

import org.ne.utrino.value.ITagValue;
import org.ne.utrino.value.IValue;

/**
 * An invocation record which can be matched against a set of signatures to
 * produce a match or an error.
 */
public interface IInvocation {

  /**
   * The number of entries in this invocation.
   */
  public int getEntryCount();

  /**
   * Returns the tag of the index'th entry. The tags must be in sorted order
   * and the same tag must not be present more than once.
   */
  public ITagValue getTag(int index);

  /**
   * Returns the value of the index'th entry.
   */
  public IValue getValue(int index);

}
