package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

abstract class MacroHandler {
  private final TreeElement<? extends JCTree> toReplace;

  protected MacroHandler(TreeElement<? extends JCTree> toReplace) {
    this.toReplace = toReplace;
  }

  void replace() {
    var replacement = getReplacement(toReplace);
    if (!ExpressionReplacer.replace(toReplace, replacement)) {
      toReplace.error("Can not use macro");
    }
  }

  abstract JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier);

  boolean validate() {
    return true;
  }
}
