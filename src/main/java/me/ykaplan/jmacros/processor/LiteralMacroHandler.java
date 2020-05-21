package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

abstract class LiteralMacroHandler<T> extends NoInitMacroHandler {
  abstract T getLiteralReplacement(TreeElement<JCTree.JCIdent> identifier);

  @Override
  JCTree.JCExpression getReplacement(TreeElement<JCTree.JCIdent> identifier) {
    var value = getLiteralReplacement(identifier);
    return identifier.getBuilder().createLiteral(value);
  }
}
