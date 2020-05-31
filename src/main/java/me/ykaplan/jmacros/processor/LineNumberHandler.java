package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class LineNumberHandler extends LiteralMacroHandler<Long> {

  LineNumberHandler(TreeElement<JCTree.JCMethodInvocation> toReplace) {
    super(toReplace);
  }

  @Override
  Long getLiteralReplacement() {
    return getItem().getLineNumber();
  }
}
