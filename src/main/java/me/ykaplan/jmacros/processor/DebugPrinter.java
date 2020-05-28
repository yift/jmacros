package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.Map;
import java.util.Optional;
import me.ykaplan.jmacros.DebugPrint;

class DebugPrinter extends AnnotationProcessable<Void> {

  protected DebugPrinter() {
    super(DebugPrint.class);
  }

  @Override
  protected Optional<Void> validateAttribute(
      String name, Object value, TreeElement<JCTree.JCAnnotation> annotation) {
    annotation.error(name + " not supported");
    return Optional.empty();
  }

  @Override
  protected void process(
      TreeElement<? extends JCTree> parent,
      TreeElement<JCTree.JCAnnotation> annotation,
      Map<String, Void> attributes) {
    System.out.println(parent);
  }
}
