package org.ne.utrino.runtime;

import java.util.List;

import org.ne.utrino.ast.IDeclaration;
import org.ne.utrino.ast.IExpression;
import org.ne.utrino.ast.ISymbol;
import org.ne.utrino.ast.ISymbol.Era;
import org.ne.utrino.ast.NameDeclaration;
import org.ne.utrino.ast.Unit;
import org.ne.utrino.syntax.Parser;
import org.ne.utrino.syntax.Token;
import org.ne.utrino.syntax.Tokenizer;
import org.ne.utrino.util.Factory;
import org.ne.utrino.util.Pair;
import org.ne.utrino.value.RPromise;
import org.ne.utrino.value.RSpace;

public class Compiler {

  public static void run(String code) {
    Runtime runtime = new Runtime(4);
    runtime.start();
    List<Token> tokens = Tokenizer.tokenize(code);
    Unit unit = Parser.parseUnit(tokens);
    compileUnit(runtime.newProcess(), unit);
  }

  private static class PhaseBuilder implements IDeclaration.IVisitor {

    private final ISymbol.Era era;
    private final List<Pair<ISymbol, IExpression>> decls = Factory.newArrayList();

    public PhaseBuilder(ISymbol.Era era) {
      this.era = era;
    }

    @Override
    public void visitNameDeclaration(NameDeclaration that) {
      ISymbol name = that.getName();
      if (name.getEra() == this.era)
        decls.add(Pair.of(name, that.getValue()));
    }

  }

  private static void compileUnit(Process process, Unit unit) {
    final PhaseBuilder builder = new PhaseBuilder(Era.PAST);
    unit.accept(builder);
    RPromise<RSpace> pastLoaded = process.schedule(new ITask<RSpace>() {
      @Override
      public RSpace execute(Process process) {
        return loadPhase(builder);
      }
    });
  }

  private static RSpace loadPhase(PhaseBuilder builder) {
    RSpace space = new RSpace();
    for (Pair<ISymbol, IExpression> entry : builder.decls) {

    }
    return space;
  }

}
