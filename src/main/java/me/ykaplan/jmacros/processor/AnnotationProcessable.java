package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

abstract class AnnotationProcessable<T> implements UnitProcessable {

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

    Map<String, T> attributes = new HashMap<>();
    if (annotation.getElement().attribute != null) {
      for (var attribute : annotation.getElement().attribute.getElementValues().entrySet()) {
        var name = attribute.getKey().getSimpleName().toString();
        var value = attribute.getValue().getValue();
        var validatedAttribute = validateAttribute(name, value, annotation);
        if (validatedAttribute.isEmpty()) {
          return;
        }
        attributes.put(name, validatedAttribute.get());
      }
    }
    process(annotation.getParent().getParent(), annotation, attributes);
  }

  protected abstract Optional<T> validateAttribute(
      String name, Object value, TreeElement<JCTree.JCAnnotation> annotation);

  protected abstract void process(
      TreeElement<? extends JCTree> parent,
      TreeElement<JCTree.JCAnnotation> annotation,
      Map<String, T> attributes);
}
