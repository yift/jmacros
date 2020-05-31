package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class IdentifierReplacer implements UnitProcessable {

  public void processUnit(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
    compilationUnitTree.forEachOfType(
        JCTree.JCMethodInvocation.class, this::processMethodInvocation);
  }

  private void processMethodInvocation(TreeElement<JCTree.JCMethodInvocation> invocation) {
    var handler = MacroHandlerFactory.createHandler(invocation);
    handler.ifPresent(h -> h.replace());
  }
}
