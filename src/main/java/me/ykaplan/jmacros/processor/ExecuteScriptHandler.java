package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.concurrent.TimeUnit;

public class ExecuteScriptHandler extends InitMacroHandler {
  private String replacement = null;

  protected ExecuteScriptHandler(TreeElement<JCTree.JCIdent> identifier) {
    super(identifier);
  }

  @Override
  boolean validateAndKeepArgument(Object argument) {
    if (!(argument instanceof String)) {
      error("Argument must be String");
      return false;
    }

    try {
      replacement = execute(argument.toString());
      return true;
    } catch (Exception e) {
      error("Can not execute '" + argument + "' " + e.getMessage());
      return false;
    }
  }

  @Override
  JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier) {
    return identifier.getBuilder().createLiteral(replacement);
  }

  private String execute(String command) throws Exception {
    var process = Runtime.getRuntime().exec(command);
    if (!process.waitFor(30, TimeUnit.SECONDS)) {
      throw new Exception("Timeout!");
    }
    var error = new String(process.getErrorStream().readAllBytes());
    if (!error.isBlank()) {
      warning(error);
    }
    if (process.exitValue() != 0) {
      throw new Exception("Execution failed");
    }
    return new String(process.getInputStream().readAllBytes());
  }
}
