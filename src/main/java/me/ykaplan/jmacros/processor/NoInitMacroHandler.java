package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.Optional;

abstract class NoInitMacroHandler extends MacroHandler {

  @Override
  Optional<String> initValueError(JCTree.JCExpression init) {
    if (init != null) {
      return Optional.of("Can not have initialization");
    }
    return Optional.empty();
  }
}
