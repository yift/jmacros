package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

abstract class InitMacroHandler extends MacroHandler {
  private final TreeElement<JCTree.JCIdent> identifier;

  protected InitMacroHandler(TreeElement<JCTree.JCIdent> identifier) {
    super(identifier.getParent());
    this.identifier = identifier;
  }

  protected void error(String error) {
    identifier.error(error);
  }

  protected void warning(String error) {
    identifier.warning(error);
  }

  abstract boolean validateAndKeepArgument(Object argument);

  @Override
  boolean validate() {
    var parent = identifier.getParent();
    if (!(parent.getElement() instanceof JCTree.JCMethodInvocation)) {
      error("One can only use " + identifier.getElement().getName() + " as a function.");
      return false;
    }
    var invocation = (JCTree.JCMethodInvocation) parent.getElement();
    if (invocation.meth != identifier.getElement()) {
      error("One can only use " + identifier.getElement().getName() + " as a function name.");
      return false;
    }
    if (invocation.args.size() != 1) {
      error("Need one argument for " + identifier.getElement().getName() + ".");
      return false;
    }
    var arg = invocation.args.get(0);
    if (!(arg instanceof JCTree.JCLiteral)) {
      error("Need literal argument for " + identifier.getElement().getName() + ".");
      return false;
    }
    return validateAndKeepArgument(((JCTree.JCLiteral) arg).value);
  }
}
