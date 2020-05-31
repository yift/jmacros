package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.net.URL;

class UrlContentAsStringHandler extends InitMacroHandler {
  private String replacement = null;

  protected UrlContentAsStringHandler(TreeElement<JCTree.JCMethodInvocation> identifier) {
    super(identifier);
  }

  @Override
  boolean validateAndKeepArgument(Object argument) {
    if (!(argument instanceof String)) {
      error("Argument must be String");
      return false;
    }
    try {
      replacement = readUrl(argument.toString());
      return true;
    } catch (Exception e) {
      error("Can not read file content: " + e.getMessage());
      return false;
    }
  }

  @Override
  JCTree.JCExpression getReplacement(TreeBuilder builder) {
    return builder.createLiteral(replacement);
  }

  private String readUrl(String urlAsString) throws Exception {
    var url = new URL(urlAsString);
    try (var reader = url.openStream(); ) {
      return new String(reader.readAllBytes());
    }
  }
}
