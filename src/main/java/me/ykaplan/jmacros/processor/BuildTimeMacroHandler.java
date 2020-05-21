package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.time.Instant;

class BuildTimeMacroHandler extends NoInitMacroHandler {
  private static Instant buildTime = Instant.now();

  BuildTimeMacroHandler(TreeElement<? extends JCTree> toReplace) {
    super(toReplace);
  }

  @Override
  JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier) {
    return identifier
        .getBuilder()
        .staticMethodInvoke("java.time.Instant.ofEpochMilli", buildTime.toEpochMilli());
  }
}
