package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.HashSet;
import java.util.Set;
import me.ykaplan.jmacros.Macro;
import me.ykaplan.jmacros.macros.LiteralMacro;

class MacrosImportsHandler {
  private static final String packageName = Macro.class.getPackage().getName();

  private static final String literalPackageName = LiteralMacro.class.getPackage().getName();
  private static final int literalPackageNameLength = literalPackageName.length() + 1;

  private final Set<String> supportedMacros;

  MacrosImportsHandler(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
    supportedMacros = new HashSet<>();
    var defs = compilationUnitTree.getElement().defs;
    for (var definition : defs) {
      if (definition instanceof JCTree.JCImport) {
        if (((JCTree.JCImport) definition).getQualifiedIdentifier()
            instanceof JCTree.JCFieldAccess) {
          var name = ((JCTree.JCImport) definition).getQualifiedIdentifier().toString();
          if (name.startsWith(packageName)) {
            compilationUnitTree.getElement().defs =
                List.filter(compilationUnitTree.getElement().defs, definition);
            if (name.startsWith(literalPackageName)) {
              var macroName = name.substring(literalPackageNameLength);
              supportedMacros.add(macroName);
            }
          }
        }
      }
    }
  }

  boolean anyMacroSupporter() {
    return !supportedMacros.isEmpty();
  }

  boolean isMacroSupported(String macroName) {
    return supportedMacros.contains(macroName) || supportedMacros.contains("*");
  }
}
