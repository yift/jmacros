package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

abstract class MacroHandler {
  private final TreeElement<? extends JCTree> toReplace;

  protected MacroHandler(TreeElement<? extends JCTree> toReplace) {
    this.toReplace = toReplace;
  }

  void replace() {
    var replacement = getReplacement(toReplace);
    ExpressionReplacer.replace(toReplace, replacement);
  }

  abstract JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier);

  boolean validate() {
    return true;
  }
}
