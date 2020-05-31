package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class ClassNameHandler extends LiteralMacroHandler<String> {
  ClassNameHandler(TreeElement<JCTree.JCMethodInvocation> toReplace) {
    super(toReplace);
  }

  @Override
  String getLiteralReplacement() {
    return getItem().getClassName();
  }
}
