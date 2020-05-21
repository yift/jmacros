package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.HashSet;
import java.util.Set;
import me.ykaplan.jmacros.macros.Macro;

class MacrosImportsHandler {
  private static final String packageName = Macro.class.getPackage().getName();
  private static final int packageNameLength = packageName.length() + 1;

  private final Set<String> supportedMacros;
  private final List<JCTree> newDefs;

  MacrosImportsHandler(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
    supportedMacros = new HashSet<>();
    var newDefs = compilationUnitTree.getElement().defs;
    for (var definition : compilationUnitTree.getElement().defs) {
      if (definition instanceof JCTree.JCImport) {
        if (((JCTree.JCImport) definition).getQualifiedIdentifier()
            instanceof JCTree.JCFieldAccess) {
          var name = ((JCTree.JCImport) definition).getQualifiedIdentifier().toString();
          if (name.startsWith(packageName)) {
            newDefs = List.filter(newDefs, definition);
            var macroName = name.substring(packageNameLength);
            supportedMacros.add(macroName);
          }
        }
      }
    }
    this.newDefs = newDefs;
  }

  boolean anyMacroSupporter() {
    return !supportedMacros.isEmpty();
  }

  List<JCTree> newDefs() {
    return newDefs;
  }

  boolean isMacroSupported(String macroName) {
    return supportedMacros.contains(macroName);
  }
}
