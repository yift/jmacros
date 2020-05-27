package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class ClassTypeMacroHandler extends MacroHandler {
  ClassTypeMacroHandler(TreeElement<? extends JCTree> toReplace) {
    super(toReplace);
  }

  @Override
  JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier) {
    var cls = identifier.getClassType();
    return identifier.getBuilder().createIdent(cls.getElement().name.toString() + ".class");
  }
}
