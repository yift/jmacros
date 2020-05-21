package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class ClassNameMacroHandler extends LiteralMacroHandler<String> {
  ClassNameMacroHandler(TreeElement<? extends JCTree> toReplace) {
    super(toReplace);
  }

  @Override
  String getLiteralReplacement(TreeElement<? extends JCTree> identifier) {
    return identifier.getClassName();
  }
}
