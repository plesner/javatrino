package org.ne.utrino.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;

/**
 * A collection of factory methods for built-in Java types, following
 * the pattern from effective java.
 */
public class Factory {

  /**
   * Creates a new array list.
   */
  public static <T> ArrayList<T> newArrayList() {
    return new ArrayList<T>();
  }

  /**
   * Creates a new hash map.
   */
  public static <K, V> HashMap<K, V> newHashMap() {
    return new HashMap<K, V>();
  }

  /**
   * Creates a new identity hash map.
   */
  public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
    return new IdentityHashMap<K, V>();
  }

  /**
   * Creates a new tree map.
   */
  public static <K, V> TreeMap<K, V> newTreeMap() {
    return new TreeMap<K, V>();
  }

  /**
   * Creates a new linked list.
   */
  public static <T> LinkedList<T> newLinkedList() {
    return new LinkedList<T>();
  }

  /**
   * Creates a new hash set.
   */
  public static <T> HashSet<T> newHashSet() {
    return new HashSet<T>();
  }

  /**
   * Creates a new stack.
   */
  public static <T> Stack<T> newStack() {
    return new Stack<T>();
  }

}
