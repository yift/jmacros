package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

abstract class AnnotationProcessable implements UnitProcessable {

  private final Class<?> annotationClass;

  protected AnnotationProcessable(Class<?> annotationClass) {
    this.annotationClass = annotationClass;
  }

  @Override
  public void processUnit(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
    compilationUnitTree.forEachOfType(JCTree.JCAnnotation.class, this::processAnnotation);
  }

  private void removeMe(TreeElement<JCTree.JCAnnotation> annotation) {
    var parent = (JCTree.JCModifiers) (annotation.getParent().getElement());
    parent.annotations = List.filter(parent.annotations, annotation.getElement());
  }

  private <R extends JCTree> void processAnnotation(TreeElement<JCTree.JCAnnotation> annotation) {
    if (annotation.getElement().type != null) {
      var type = annotation.getElement().type.toString();
      if (!type.equals(annotationClass.getCanonicalName())) {
        return;
      }
    } else if (annotation.getElement().annotationType instanceof JCTree.JCIdent) {
      var type = annotation.getElement().annotationType.toString();
      if (!type.equals(annotationClass.getSimpleName())) {
        return;
      }
    } else {
      return;
    }

    removeMe(annotation);

    var attributes =
        Optional.ofNullable(annotation.getElement().attribute)
            .map(
                atts ->
                    atts.getElementValues().entrySet().stream()
                        .filter(Objects::nonNull)
                        .map(
                            attribute ->
                                Map.entry(
                                    attribute.getKey().getSimpleName().toString(),
                                    attribute.getValue().getValue()))
                        .filter(
                            attribute ->
                                validateAttribute(
                                    attribute.getKey(), attribute.getValue(), annotation))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
            .orElse(Map.of());
    process(annotation.getParent().getParent(), annotation, attributes);
  }

  protected abstract boolean validateAttribute(
      String name, Object value, TreeElement<JCTree.JCAnnotation> annotation);

  protected abstract void process(
      TreeElement<? extends JCTree> parent,
      TreeElement<JCTree.JCAnnotation> annotation,
      Map<String, Object> attributes);
}
