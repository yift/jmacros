package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Name;

class TreeMover {
  static <T extends JCTree> void move(T tree, TreeElement<?> element) {
    new TreeScanner() {
      @Override
      public void scan(JCTree tree) {
        if (tree != null) {
          tree.pos = element.getElement().pos;
        }
        super.scan(tree);
      }

      @Override
      public void visitIdent(JCTree.JCIdent tree) {
        tree.name = element.getBuilder().createName(tree.name);
        super.visitIdent(tree);
      }

      @Override
      public void visitTypeParameter(JCTree.JCTypeParameter tree) {
        tree.name = element.getBuilder().createName(tree.name);
        super.visitTypeParameter(tree);
      }

      @Override
      public void visitClassDef(JCTree.JCClassDecl tree) {
        tree.name = element.getBuilder().createName(tree.name);
        super.visitClassDef(tree);
      }

      @Override
      public void visitMethodDef(JCTree.JCMethodDecl tree) {
        tree.name = element.getBuilder().createName(tree.name);
        super.visitMethodDef(tree);
      }

      @Override
      public void visitVarDef(JCTree.JCVariableDecl tree) {
        tree.name = element.getBuilder().createName(tree.name);
        super.visitVarDef(tree);
      }

      @Override
      public void visitLabelled(JCTree.JCLabeledStatement tree) {
        tree.label = element.getBuilder().createName(tree.label);
        super.visitLabelled(tree);
      }

      @Override
      public void visitBreak(JCTree.JCBreak tree) {
        tree.label = element.getBuilder().createName(tree.label);
        super.visitBreak(tree);
      }

      @Override
      public void visitContinue(JCTree.JCContinue tree) {
        tree.label = element.getBuilder().createName(tree.label);
        super.visitContinue(tree);
      }

      @Override
      public void visitSelect(JCTree.JCFieldAccess tree) {
        tree.name = element.getBuilder().createName(tree.name);
        super.visitSelect(tree);
      }

      @Override
      public void visitReference(JCTree.JCMemberReference tree) {
        tree.name = element.getBuilder().createName(tree.name);
        super.visitReference(tree);
      }

      @Override
      public void visitLiteral(JCTree.JCLiteral tree) {
        if (tree.value instanceof Name) {
          var value = tree.value.toString();
          switch (tree.typetag) {
            case INT:
            case CHAR:
            case BOOLEAN:
            case BYTE:
            case SHORT:
              tree.value = Integer.parseInt(value);
              break;
            case CLASS:
              tree.value = value;
              break;
            case LONG:
              tree.value = Long.parseLong(value);
              break;
            case DOUBLE:
              tree.value = Double.parseDouble(value);
              break;
            case FLOAT:
              tree.value = Float.parseFloat(value);
              break;
            default:
              element.error("Could not set literal " + value + " of type " + tree.typetag);
          }
        }
        super.visitLiteral(tree);
      }
    }.scan(tree);
  }
}
