package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

abstract class MacroHandler {
  private final TreeElement<? extends JCTree> toReplace;

  protected MacroHandler(TreeElement<? extends JCTree> toReplace) {
    this.toReplace = toReplace;
  }

  void replace() {
    var replacement = getReplacement(toReplace);
    var parent = toReplace.getParent();
    boolean set = false;
    if (parent.getElement() instanceof JCTree.JCVariableDecl) {
      var declaration = (JCTree.JCVariableDecl) parent.getElement();
      if (declaration.init == toReplace.getElement()) {
        declaration.init = replacement;
        set = true;
      }
    } else if (parent.getElement() instanceof JCTree.JCBinary) {
      var binary = (JCTree.JCBinary) parent.getElement();
      if (binary.lhs == toReplace.getElement()) {
        binary.lhs = replacement;
        set = true;
      }
      if (binary.rhs == toReplace.getElement()) {
        binary.rhs = replacement;
        set = true;
      }
    } else if (parent.getElement() instanceof JCTree.JCMethodInvocation) {
      var methodInvocation = (JCTree.JCMethodInvocation) parent.getElement();
      List<JCTree.JCExpression> newArgs = List.nil();
      for (var expression : methodInvocation.args) {
        if (expression == toReplace.getElement()) {
          newArgs = newArgs.append(replacement);
          set = true;
        } else {
          newArgs = newArgs.append(expression);
        }
      }
      methodInvocation.args = newArgs;
    } else if (parent.getElement() instanceof JCTree.JCFieldAccess) {
      var fieldAccess = (JCTree.JCFieldAccess) parent.getElement();
      if (fieldAccess.selected == toReplace.getElement()) {
        fieldAccess.selected = replacement;
        set = true;
      }
    }
    if (!set) {
      toReplace.error("Can not use macro");
    }
  }

  abstract JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier);

  boolean validate() {
    return true;
  }
}
