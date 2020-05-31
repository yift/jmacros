package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

abstract class InitMacroHandler extends MacroHandler {

  private final TreeElement<JCTree.JCMethodInvocation> item;

  protected InitMacroHandler(TreeElement<JCTree.JCMethodInvocation> item) {
    super(item);
    this.item = item;
  }

  protected void error(String error) {
    item.error(error);
  }

  protected void warning(String error) {
    item.warning(error);
  }

  abstract boolean validateAndKeepArgument(Object argument);

  @Override
  boolean validate() {
    var invocation = item.getElement();
    if (invocation.args.size() != 1) {
      error("Need an argument.");
      return false;
    }
    var arg = invocation.args.get(0);
    if (!(arg instanceof JCTree.JCLiteral)) {
      error("Need literal argument.");
      return false;
    }
    return validateAndKeepArgument(((JCTree.JCLiteral) arg).value);
  }
}
