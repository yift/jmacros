package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class MethodNameMacroHandler extends LiteralMacroHandler<String> {
  MethodNameMacroHandler(TreeElement<? extends JCTree> toReplace) {
    super(toReplace);
  }

  @Override
  String getLiteralReplacement(TreeElement<? extends JCTree> identifier) {
    return identifier.getMethodName();
  }
}
