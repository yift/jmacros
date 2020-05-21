package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class LineNumberMacroHandler extends LiteralMacroHandler<Long> {
  public LineNumberMacroHandler() {}

  @Override
  Long getLiteralReplacement(TreeElement<JCTree.JCIdent> identifier) {
    return identifier.getLineNumber();
  }
}
