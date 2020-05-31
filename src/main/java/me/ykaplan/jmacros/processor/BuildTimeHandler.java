package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import me.ykaplan.jmacros.LiteralMacro;

class BuildTimeHandler extends MacroHandler {

  BuildTimeHandler(TreeElement<JCTree.JCMethodInvocation> toReplace) {
    super(toReplace);
  }

  @Override
  JCTree.JCExpression getReplacement(TreeBuilder builder) {
    return builder.staticMethodInvoke(
        "java.time.Instant.ofEpochMilli", LiteralMacro.buildTime().toEpochMilli());
  }
}
