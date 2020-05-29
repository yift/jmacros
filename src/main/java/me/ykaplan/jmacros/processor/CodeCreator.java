package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.Pretty;
import java.io.IOException;
import java.io.StringWriter;

class CodeCreator {
  private CodeCreator() {}

  static String getCode(JCTree tree, TreeElement<? extends JCTree> element) {
    StringWriter s = new StringWriter();
    try {
      new Pretty(s, false) {
        @Override
        public void visitVarDef(JCTree.JCVariableDecl tree) {
          boolean varSet = false;
          if (tree.vartype == null) {
            tree.vartype = element.getBuilder().createIdent("var");
            varSet = true;
          }
          super.visitVarDef(tree);
          if (varSet) {
            tree.vartype = null;
          }
        }
      }.printExpr(tree);
    } catch (IOException e) {
      element.error("Could not extract code: " + e.getMessage());
    }
    return s.toString();
  }
}
