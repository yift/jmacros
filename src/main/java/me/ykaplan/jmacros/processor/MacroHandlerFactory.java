package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.Optional;
import me.ykaplan.jmacros.macros.*;

class MacroHandlerFactory {
  private final MacrosImportsHandler macrosImportsHandler;

  public MacroHandlerFactory(MacrosImportsHandler macrosImportsHandler) {
    this.macrosImportsHandler = macrosImportsHandler;
  }

  public Optional<MacroHandler> createHandler(TreeElement<JCTree.JCIdent> ident) {
    var tree = ident.getElement();
    var name = tree.getName().toString();
    if (!macrosImportsHandler.isMacroSupported(name)) {
      return Optional.empty();
    }
    MacroHandler handler = null;
    if (name.equals(LineNumber.class.getSimpleName())) {
      handler = new LineNumberMacroHandler(ident);
    } else if (name.equals(FileName.class.getSimpleName())) {
      handler = new FileNameMacroHandler(ident);
    } else if (name.equals(MethodName.class.getSimpleName())) {
      handler = new MethodNameMacroHandler(ident);
    } else if (name.equals(ClassName.class.getSimpleName())) {
      handler = new ClassNameMacroHandler(ident);
    } else if (name.equals(BuildTime.class.getSimpleName())) {
      handler = new BuildTimeMacroHandler(ident);
    }
    return Optional.ofNullable(handler);
  }
}
