package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class FileNameHandler extends LiteralMacroHandler<String> {
  private final String fileName;

  FileNameHandler(TreeElement<JCTree.JCMethodInvocation> variable) {
    super(variable);
    fileName = variable.getFileName();
  }

  @Override
  String getLiteralReplacement() {
    return fileName;
  }
}
