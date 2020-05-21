package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class FileNameMacroHandler extends LiteralMacroHandler<String> {
  private final String fileName;

  FileNameMacroHandler(TreeElement<? extends JCTree> variable) {
    super(variable);
    fileName = variable.getFileName();
  }

  @Override
  String getLiteralReplacement(TreeElement<? extends JCTree> identifier) {
    return fileName;
  }
}
