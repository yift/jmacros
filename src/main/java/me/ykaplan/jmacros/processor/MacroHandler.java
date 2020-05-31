package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

abstract class MacroHandler {
  private final TreeElement<JCTree.JCMethodInvocation> toReplace;

  protected MacroHandler(TreeElement<JCTree.JCMethodInvocation> toReplace) {
    this.toReplace = toReplace;
  }

  void replace() {
    var replacement = getReplacement(toReplace.getBuilder());
    ExpressionReplacer.replace(toReplace, replacement);
  }

  protected TreeElement<JCTree.JCMethodInvocation> getItem() {
    return toReplace;
  }

  abstract JCTree.JCExpression getReplacement(TreeBuilder builder);

  boolean validate() {
    return true;
  }
}
