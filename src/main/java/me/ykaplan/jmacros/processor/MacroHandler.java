package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.Optional;

abstract class MacroHandler {
  abstract Optional<String> initValueError(JCTree.JCExpression init);

  abstract JCTree.JCExpression getReplacement(TreeElement<JCTree.JCIdent> identifier);
}
