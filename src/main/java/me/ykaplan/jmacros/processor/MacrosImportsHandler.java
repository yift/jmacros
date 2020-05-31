package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.HashMap;
import java.util.Map;
import me.ykaplan.jmacros.LiteralMacro;
import me.ykaplan.jmacros.Macro;

class MacrosImportsHandler {
  private static final String packageName = Macro.class.getPackage().getName();
  private static final int packageNameLength = packageName.length() + 1;
  private static final String literalClassName = LiteralMacro.class.getSimpleName();

  private final Map<String, Boolean> imports = new HashMap<>();

  MacrosImportsHandler(JCTree.JCCompilationUnit compilationUnitTree) {
    for (var definition : compilationUnitTree.defs) {
      if (definition instanceof JCTree.JCImport) {
        var importDefinition = (JCTree.JCImport) definition;
        if (importDefinition.getQualifiedIdentifier() instanceof JCTree.JCFieldAccess) {
          var name = importDefinition.getQualifiedIdentifier().toString();
          if (name.startsWith(packageName)) {
            compilationUnitTree.defs = List.filter(compilationUnitTree.defs, definition);
            imports.put(name.substring(packageNameLength), importDefinition.staticImport);
          }
        }
      }
    }
  }

  boolean isMacroSupported(Class<?> cls) {
    return isMacroSupported(cls.getSimpleName());
  }

  boolean isMacroSupported(String macroName) {
    return imports.containsKey(macroName) || imports.containsKey("*");
  }

  boolean isLiteralFunctionSupported(String functionName) {
    if (isMacroSupported(literalClassName + "." + functionName)) {
      return true;
    }
    return imports.containsKey(literalClassName + ".*");
  }
}
