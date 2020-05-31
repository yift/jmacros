package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class MethodNameHandler extends LiteralMacroHandler<String> {
  MethodNameHandler(TreeElement<JCTree.JCMethodInvocation> toReplace) {
    super(toReplace);
  }

  @Override
  String getLiteralReplacement() {
    return getItem().getMethodName();
  }
}
