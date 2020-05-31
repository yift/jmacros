package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

abstract class LiteralMacroHandler<T> extends MacroHandler {
  LiteralMacroHandler(TreeElement<JCTree.JCMethodInvocation> toReplace) {
    super(toReplace);
  }

  abstract T getLiteralReplacement();

  @Override
  JCTree.JCExpression getReplacement(TreeBuilder builder) {
    T value = getLiteralReplacement();
    return builder.createLiteral(value);
  }
}
