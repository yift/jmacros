package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class IdentifierReplacer implements UnitProcessable {

  public void processUnit(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
    var imports = new MacrosImportsHandler(compilationUnitTree);
    if (!imports.anyMacroSupporter()) {
      // Nothing to do
      return;
    }

    var macroHandlerFactory = new MacroHandlerFactory(imports);
    compilationUnitTree.forEachOfType(
        JCTree.JCIdent.class, ident -> processIdentifier(ident, macroHandlerFactory));
  }

  private void processIdentifier(
      TreeElement<JCTree.JCIdent> ident, MacroHandlerFactory macroHandlerFactory) {
    var handler = macroHandlerFactory.createHandler(ident);
    handler.ifPresent(h -> h.replace());
  }
}
