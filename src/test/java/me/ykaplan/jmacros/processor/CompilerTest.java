package me.ykaplan.jmacros.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.sun.tools.javac.tree.JCTree;
import org.junit.jupiter.api.Test;

public class CompilerTest {
  @Test
  public void noSuchClass() {
    @SuppressWarnings("unchecked")
    TreeElement<JCTree> element = mock(TreeElement.class);
    var code = "class Aaaa {}";
    var className = "Bbbb";
    var compiler = new Compiler(element, code, className);

    var invoker = compiler.createInvoker();

    assertThat(invoker).isEmpty();
  }
}
