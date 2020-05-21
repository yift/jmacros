package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

public class BuildEnvironmentVariableHandler extends InitMacroHandler {
  private String replacement = null;

  protected BuildEnvironmentVariableHandler(TreeElement<JCTree.JCIdent> identifier) {
    super(identifier);
  }

  @Override
  boolean validateAndKeepArgument(Object argument) {
    if (!(argument instanceof String)) {
      error("Argument must be String");
      return false;
    }
    replacement = System.getenv(argument.toString());
    if (replacement == null) {
      replacement = "";
    }
    return true;
  }

  @Override
  JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier) {
    return identifier.getBuilder().createLiteral(replacement);
  }
}
