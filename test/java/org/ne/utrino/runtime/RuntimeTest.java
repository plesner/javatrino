package org.ne.utrino.runtime;

import junit.framework.TestCase;

public class RuntimeTest extends TestCase {

  public void testRun() {
    Compiler.run("def $x := 4;");
  }

}
