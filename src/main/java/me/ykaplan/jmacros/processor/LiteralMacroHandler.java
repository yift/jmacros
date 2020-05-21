package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

abstract class LiteralMacroHandler<T> extends MacroHandler {
  LiteralMacroHandler(TreeElement<? extends JCTree> toReplace) {
    super(toReplace);
  }

  abstract T getLiteralReplacement(TreeElement<? extends JCTree> identifier);

  @Override
  JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier) {
    var value = getLiteralReplacement(identifier);
    return identifier.getBuilder().createLiteral(value);
  }
}
