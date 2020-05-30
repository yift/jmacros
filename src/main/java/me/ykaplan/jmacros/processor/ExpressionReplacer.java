package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

class ExpressionReplacer {

  static void replace(TreeElement<? extends JCTree> toReplace, JCTree.JCExpression replacement) {
    var parent = toReplace.getParent().getElement();
    if (parent instanceof JCTree.JCVariableDecl) {
      var declaration = (JCTree.JCVariableDecl) parent;
      if (declaration.init == toReplace.getElement()) {
        declaration.init = replacement;
        return;
      }
    } else if (parent instanceof JCTree.JCBinary) {
      var binary = (JCTree.JCBinary) parent;
      if (binary.lhs == toReplace.getElement()) {
        binary.lhs = replacement;
        return;
      }
      if (binary.rhs == toReplace.getElement()) {
        binary.rhs = replacement;
        return;
      }
    } else if (parent instanceof JCTree.JCMethodInvocation) {
      var methodInvocation = (JCTree.JCMethodInvocation) parent;
      List<JCTree.JCExpression> newArgs = List.nil();
      boolean set = false;
      for (var expression : methodInvocation.args) {
        if (expression == toReplace.getElement()) {
          newArgs = newArgs.append(replacement);
          set = true;
        } else {
          newArgs = newArgs.append(expression);
        }
      }
      if (set) {
        methodInvocation.args = newArgs;
        return;
      }
    } else if (parent instanceof JCTree.JCFieldAccess) {
      var fieldAccess = (JCTree.JCFieldAccess) parent;
      if (fieldAccess.selected == toReplace.getElement()) {
        fieldAccess.selected = replacement;
        return;
      }
    } else if (parent instanceof JCTree.JCArrayAccess) {
      var arrayAccess = (JCTree.JCArrayAccess) parent;
      if (arrayAccess.indexed == toReplace.getElement()) {
        arrayAccess.indexed = replacement;
        return;
      }
      if (arrayAccess.index == toReplace.getElement()) {
        arrayAccess.index = replacement;
        return;
      }
    } else if (parent instanceof JCTree.JCReturn) {
      var ret = (JCTree.JCReturn) parent;
      if (ret.expr == toReplace.getElement()) {
        ret.expr = replacement;
        return;
      }
    } else if (parent instanceof JCTree.JCTypeCast) {
      var cast = (JCTree.JCTypeCast) parent;
      if (cast.getExpression() == toReplace.getElement()) {
        cast.expr = replacement;
        return;
      }
    } else if (parent instanceof JCTree.JCAssign) {
      var assign = (JCTree.JCAssign) parent;
      if (assign.getExpression() == toReplace.getElement()) {
        assign.rhs = replacement;
        return;
      }
    } else if (parent instanceof JCTree.JCNewClass) {
      var newClass = (JCTree.JCNewClass) parent;
      if (newClass.clazz == toReplace.getElement()) {
        newClass.clazz = replacement;
        return;
      }
    } else if (parent instanceof JCTree.JCParens) {
      var parens = (JCTree.JCParens) parent;
      if (parens.expr == toReplace.getElement()) {
        parens.expr = replacement;
        return;
      }
    }
    toReplace.error(
        "Replacing this kind of element in not yet supported; please report an issue in https://github.com/yift/jmacros");
  }
}
