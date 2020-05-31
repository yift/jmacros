package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class ClassTypeHandler extends MacroHandler {
  ClassTypeHandler(TreeElement<JCTree.JCMethodInvocation> toReplace) {
    super(toReplace);
  }

  @Override
  JCTree.JCExpression getReplacement(TreeBuilder builder) {
    var cls = getItem().getClassType();
    return builder.createIdent(cls.getElement().name.toString() + ".class");
  }
}
