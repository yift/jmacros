package me.ykaplan.jmacros.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sun.tools.javac.tree.JCTree;
import org.junit.jupiter.api.Test;

public class InterpolationExpanderTest {
  @Test
  public void notAStringValue() {
    var expander = new InterpolationExpander();
    @SuppressWarnings("unchecked")
    TreeElement<JCTree.JCAnnotation> annotation =
        (TreeElement<JCTree.JCAnnotation>) mock(TreeElement.class);
    var attribute = expander.validateAttribute("startsWith", null, annotation);

    assertThat(attribute).isEmpty();
    verify(annotation).error(anyString());
  }
}
