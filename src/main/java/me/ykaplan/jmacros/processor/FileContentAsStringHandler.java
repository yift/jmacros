package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileContentAsStringHandler extends InitMacroHandler {
  private String replacement = null;

  protected FileContentAsStringHandler(TreeElement<JCTree.JCIdent> identifier) {
    super(identifier);
  }

  @Override
  boolean validateAndKeepArgument(Object argument) {
    if (!(argument instanceof String)) {
      error("Argument must be String");
      return false;
    }
    try {
      replacement = readFile(argument.toString());
      return true;
    } catch (Exception e) {
      error("Can not read file content: " + e.getMessage());
      return false;
    }
  }

  @Override
  JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier) {
    return identifier.getBuilder().createLiteral(replacement);
  }

  private String readFile(String filePath) throws IOException {
    var path = Paths.get(filePath);
    return Files.readString(path);
  }
}
