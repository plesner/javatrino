package org.ne.utrino.syntax;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ne.utrino.util.Assert;

/**
 * A syntax tree node.
 */
public abstract class Ast implements IAstOrArguments {

  @Override
  public List<Ast> asArguments() {
    return Arrays.asList(this);
  }

  @Override
  public Ast asAst() {
    return this;
  }

  /**
   * An identifier reference.
   */
  public static class Identifier extends Ast {

    private final Object name;

    public Identifier(Object name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name.toString();
    }

  }

  public static class Literal extends Ast {

    private final Object value;

    public Literal(Object value) {
      this.value = value;
    }

  }

  /**
   * A sequence of expressions.
   */
  public static class Block extends Ast {

    private final List<Ast> children;

    public Block(List<Ast> children) {
      this.children = children;
    }

    public static Ast create(List<Ast> children) {
      if (children.isEmpty()) {
        return new Literal(null);
      } else if (children.size() == 1) {
        return children.get(0);
      } else {
        return new Block(children);
      }
    }

    @Override
    public String toString() {
      return "(;" + toString(children) + ")";
    }

  }

  public static class Call extends Ast {

    private final Ast receiver;
    private final String op;
    private final List<Ast> args;

    public Call(Ast receiver, String op, List<Ast> args) {
      this.receiver = receiver;
      this.op = op;
      this.args = args;
    }

    public List<Ast> getArguments() {
      return args;
    }

  }

  public static class Tuple extends Ast {

    private final List<Ast> asts;

    public Tuple(List<Ast> asts) {
      this.asts = asts;
    }

  }

  public static class Arguments implements IAstOrArguments {

    private final List<Ast> children;

    public Arguments(List<Ast> children) {
      this.children = children;
    }

    public static IAstOrArguments create(List<Ast> children) {
      if (children.size() == 1) {
        return children.get(0);
      } else {
        return new Arguments(children);
      }
    }

    @Override
    public List<Ast> asArguments() {
      return children;
    }

    @Override
    public Ast asAst() {
      Assert.equals(1, children.size());
      return children.get(0);
    }

  }

  /**
   * A function closure.
   */
  public static class Lambda extends Ast {

    private final Ast body;
    private final List<String> params;

    public Lambda(List<String> params, Ast body) {
      this.body = body;
      this.params = params;
    }

  }

  /**
   * A generic definition.
   */
  public static abstract class Definition extends Ast {

    private final List<Ast> annots;
    private final Object name;
    private final Ast value;
    private final Ast body;

    public Definition(List<Ast> annots, Object name, Ast value, Ast body) {
      this.annots = annots;
      this.name = name;
      this.value = value;
      this.body = body;
    }

    protected List<Ast> getAnnotations() {
      return this.annots;
    }

    protected Object getName() {
      return this.name;
    }

    protected Ast getValue() {
      return this.value;
    }

    protected Ast getBody() {
      return this.body;
    }

  }

  /**
   * A locally scoped definition.
   */
  public static class LocalDefinition extends Definition {

    public LocalDefinition(List<Ast> annots, Object name, Ast value, Ast body) {
      super(annots, name, value, body);
    }

  }

  /**
   * A toplevel definition.
   */
  public static class ToplevelDefinition extends Definition {

    public ToplevelDefinition(List<Ast> annots, Object name, Ast value, Ast body) {
      super(annots, name, value, body);
    }

  }

  /**
   * Converts a list of objects to a string. Before each element is
   * put a single whitespace.
   */
  static String toString(List<?> objs) {
    StringBuilder result = new StringBuilder();
    for (Object obj : objs) {
      result.append(" ").append(obj);
    }
    return result.toString();
  }

}
