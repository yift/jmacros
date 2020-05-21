package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class MethodNameMacroHandler extends LiteralMacroHandler<String> {
  @Override
  String getLiteralReplacement(TreeElement<JCTree.JCIdent> identifier) {
    return identifier.getMethodName();
  }
}
