package me.ykaplan.jmacros.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import me.ykaplan.jmacros.DebugPrint;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class AnnotationProcessableTest {
  @Test
  @SuppressWarnings("unchecked")
  public void typeNotIdent() {
    var processable =
        new AnnotationProcessable<String>(DebugPrint.class) {
          @Override
          protected Optional<String> validateAttribute(
              String name, Object value, TreeElement<JCTree.JCAnnotation> annotation) {
            return Optional.empty();
          }

          @Override
          protected void process(
              TreeElement<? extends JCTree> parent,
              TreeElement<JCTree.JCAnnotation> annotation,
              Map<String, String> attributes) {}
        };

    TreeElement<JCTree.JCCompilationUnit> compilationUnitTree = mock(TreeElement.class);
    processable.processUnit(compilationUnitTree);
    var argument = ArgumentCaptor.forClass(Consumer.class);
    verify(compilationUnitTree).forEachOfType(any(), argument.capture());

    var consumer = argument.getValue();
    TreeElement<JCTree.JCAnnotation> annotation =
        (TreeElement<JCTree.JCAnnotation>) mock(TreeElement.class);
    var annotationTree = mock(JCTree.JCAnnotation.class);
    doReturn(annotationTree).when(annotation).getElement();

    consumer.accept(annotation);

    verify(annotation, never()).getParent();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void typeIdentWrongName() {
    var processable =
        new AnnotationProcessable<String>(DebugPrint.class) {
          @Override
          protected Optional<String> validateAttribute(
              String name, Object value, TreeElement<JCTree.JCAnnotation> annotation) {
            return Optional.empty();
          }

          @Override
          protected void process(
              TreeElement<? extends JCTree> parent,
              TreeElement<JCTree.JCAnnotation> annotation,
              Map<String, String> attributes) {}
        };

    TreeElement<JCTree.JCCompilationUnit> compilationUnitTree = mock(TreeElement.class);
    processable.processUnit(compilationUnitTree);
    var argument = ArgumentCaptor.forClass(Consumer.class);
    verify(compilationUnitTree).forEachOfType(any(), argument.capture());

    var consumer = argument.getValue();
    TreeElement<JCTree.JCAnnotation> annotation =
        (TreeElement<JCTree.JCAnnotation>) mock(TreeElement.class);
    var annotationTree = mock(JCTree.JCAnnotation.class);
    doReturn(annotationTree).when(annotation).getElement();
    var ident = mock(JCTree.JCIdent.class);
    annotationTree.annotationType = ident;
    doReturn("").when(ident).toString();

    consumer.accept(annotation);

    verify(annotation, never()).getParent();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void typeIdentCorrectName() {
    var processable =
        new AnnotationProcessable<String>(DebugPrint.class) {
          @Override
          protected Optional<String> validateAttribute(
              String name, Object value, TreeElement<JCTree.JCAnnotation> annotation) {
            return Optional.empty();
          }

          @Override
          protected void process(
              TreeElement<? extends JCTree> parent,
              TreeElement<JCTree.JCAnnotation> annotation,
              Map<String, String> attributes) {}
        };

    TreeElement<JCTree.JCCompilationUnit> compilationUnitTree = mock(TreeElement.class);
    processable.processUnit(compilationUnitTree);
    var argument = ArgumentCaptor.forClass(Consumer.class);
    verify(compilationUnitTree).forEachOfType(any(), argument.capture());

    var consumer = argument.getValue();
    TreeElement<JCTree.JCAnnotation> annotation =
        (TreeElement<JCTree.JCAnnotation>) mock(TreeElement.class);
    var annotationTree = mock(JCTree.JCAnnotation.class);
    doReturn(annotationTree).when(annotation).getElement();
    var ident = mock(JCTree.JCIdent.class);
    annotationTree.annotationType = ident;
    doReturn(DebugPrint.class.getSimpleName()).when(ident).toString();
    var modifiers = mock(JCTree.JCModifiers.class);
    var parent = mock(TreeElement.class);
    doReturn(modifiers).when(parent).getElement();
    doReturn(parent).when(annotation).getParent();
    modifiers.annotations = List.nil();
    modifiers.annotations.append(annotationTree);

    consumer.accept(annotation);

    assertThat(modifiers.annotations).isEmpty();
  }
}
