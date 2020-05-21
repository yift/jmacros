package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

class FileNameMacroHandler extends LiteralMacroHandler<String> {
  private final String fileName;

  FileNameMacroHandler(TreeElement<JCTree.JCVariableDecl> variable) {
    fileName = variable.getFileName();
  }

  @Override
  String getLiteralReplacement(TreeElement<JCTree.JCIdent> identifier) {
    return fileName;
  }
}
