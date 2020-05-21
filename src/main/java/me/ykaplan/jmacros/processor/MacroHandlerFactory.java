package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.Optional;
import me.ykaplan.jmacros.macros.*;

class MacroHandlerFactory {
  private final MacrosImportsHandler macrosImportsHandler;

  public MacroHandlerFactory(MacrosImportsHandler macrosImportsHandler) {
    this.macrosImportsHandler = macrosImportsHandler;
  }

  public Optional<MacroHandler> createHandler(TreeElement<JCTree.JCVariableDecl> variable) {
    var tree = variable.getElement();
    if (!(tree.getType() instanceof JCTree.JCIdent)) {
      // Not an explicit ident type, no need to continue.
      return Optional.empty();
    }
    var ident = (JCTree.JCIdent) tree.getType();
    var name = ident.getName().toString();
    if (!macrosImportsHandler.isMacroSupported(name)) {
      return Optional.empty();
    }
    MacroHandler handler = null;
    if (name.equals(LineNumber.class.getSimpleName())) {
      handler = new LineNumberMacroHandler();
    } else if (name.equals(FileName.class.getSimpleName())) {
      handler = new FileNameMacroHandler(variable);
    } else if (name.equals(MethodName.class.getSimpleName())) {
      handler = new MethodNameMacroHandler();
    } else if (name.equals(ClassName.class.getSimpleName())) {
      handler = new ClassNameMacroHandler();
    } else if (name.equals(BuildTime.class.getSimpleName())) {
      handler = new BuildTimeMacroHandler();
    }

    if (handler != null) {
      var error = handler.initValueError(tree.init);
      if (error.isPresent()) {
        variable.error(error.get());
        return Optional.empty();
      }
    }
    return Optional.ofNullable(handler);
  }
}
