package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class LineNumberMacroHandler extends LiteralMacroHandler<Long> {

  LineNumberMacroHandler(TreeElement<? extends JCTree> toReplace) {
    super(toReplace);
  }

  @Override
  Long getLiteralReplacement(TreeElement<? extends JCTree> identifier) {
    return identifier.getLineNumber();
  }
}
