package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.Map;
import me.ykaplan.jmacros.DebugPrint;

class DebugPrinter extends AnnotationProcessable {

  protected DebugPrinter() {
    super(DebugPrint.class);
  }

  @Override
  protected boolean validateAttribute(
      String name, Object value, TreeElement<JCTree.JCAnnotation> annotation) {
    annotation.error(name + " not supported");
    return false;
  }

  @Override
  protected void process(
      TreeElement<? extends JCTree> parent,
      TreeElement<JCTree.JCAnnotation> annotation,
      Map<String, Object> attributes) {
    System.out.println(parent);
  }
}
