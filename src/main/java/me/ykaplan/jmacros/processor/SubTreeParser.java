package me.ykaplan.jmacros.processor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.tree.JCTree;
import java.util.concurrent.atomic.AtomicReference;

class SubTreeParser extends InternalCompiler {
  private final TreeElement<?> element;
  private final AtomicReference<JCTree.JCExpression> expression = new AtomicReference<>();

  public SubTreeParser(String code, TreeElement<?> element) {
    super("class Test { public Object go() {return " + code + ";}}", "Test");
    this.element = element;
  }

  @Override
  public void gotError(String error) {
    if (expression.get() == null) {
      element.error(error);
    }
  }

  @Override
  public void gotWarning(String warning) {
    if (expression.get() == null) {
      element.warning(warning);
    }
  }

  @Override
  public void parsed(CompilationUnitTree unit) {
    expression.set(findExpression(unit));
  }

  JCTree.JCExpression parse() {
    compile();
    return expression.get();
  }

  private JCTree.JCExpression findExpression(CompilationUnitTree compiledTree) {
    var tree = (JCTree.JCCompilationUnit) compiledTree;
    if ((tree.defs.size() != 1) || (!(tree.defs.get(0) instanceof JCTree.JCClassDecl))) {
      element.error("Invalid expression");
      return null;
    }
    var classDecl = (JCTree.JCClassDecl) tree.defs.get(0);
    if ((classDecl.defs.size() != 1) || (!(classDecl.defs.get(0) instanceof JCTree.JCMethodDecl))) {
      element.error("Invalid expression");
      return null;
    }
    JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) classDecl.defs.get(0);
    if ((method.body.stats.size() != 1)
        || (!(method.body.stats.get(0) instanceof JCTree.JCReturn))) {
      element.error("Invalid expression");
      return null;
    }
    var retStatement = (JCTree.JCReturn) method.body.stats.get(0);
    var expression = retStatement.getExpression();
    TreeMover.move(expression, element);

    return expression;
  }
}
