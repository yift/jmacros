package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.concurrent.TimeUnit;

class ExecuteScriptHandler extends InitMacroHandler {
  private String replacement = null;
  private final Runtime runtime;

  ExecuteScriptHandler(TreeElement<JCTree.JCMethodInvocation> identifier, Runtime runtime) {
    super(identifier);
    this.runtime = runtime;
  }

  protected ExecuteScriptHandler(TreeElement<JCTree.JCMethodInvocation> identifier) {
    this(identifier, Runtime.getRuntime());
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
  JCTree.JCExpression getReplacement(TreeBuilder builder) {
    return builder.createLiteral(replacement);
  }

  private String execute(String command) throws Exception {
    var commands = command.split("\\s+");
    var process = runtime.exec(commands);
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
