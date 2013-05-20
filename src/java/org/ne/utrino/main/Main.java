package org.ne.utrino.main;

import org.ne.utrino.runtime.Compiler;

/**
 * Main entry-point for the command-line.
 */
public class Main {

  public static void main(String[] args) {
    Compiler.run(args[1]);
  }

}
