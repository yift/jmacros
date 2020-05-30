package me.ykaplan.jmacros.processor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.sun.tools.javac.tree.JCTree;
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
}
