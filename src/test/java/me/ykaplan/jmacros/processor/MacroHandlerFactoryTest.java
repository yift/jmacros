package me.ykaplan.jmacros.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Name;
import org.junit.jupiter.api.Test;

public class MacroHandlerFactoryTest {
  @Test
  public void exceptionInCreation() {
    var invocation = mock(JCTree.JCMethodInvocation.class);
    var ident = mock(JCTree.JCIdent.class);
    var name = mock(Name.class);
    doReturn("fileName").when(name).toString();
    ident.name = name;
    invocation.meth = ident;
    @SuppressWarnings("unchecked")
    var element = (TreeElement<JCTree.JCMethodInvocation>) mock(TreeElement.class);
    doReturn(invocation).when(element).getElement();
    var imports = mock(MacrosImportsHandler.class);
    doReturn(true).when(imports).isLiteralFunctionSupported("fileName");
    doReturn(imports).when(element).getImports();
    doThrow(new RuntimeException()).when(element).getFileName();

    var handler = MacroHandlerFactory.createHandler(element);

    assertThat(handler).isEmpty();
    verify(element).error(anyString());
  }

  @Test
  public void unsupportedLiteral() {
    var invocation = mock(JCTree.JCMethodInvocation.class);
    var ident = mock(JCTree.JCIdent.class);
    var name = mock(Name.class);
    doReturn("LiteralMacro.fileName").when(name).toString();
    ident.name = name;
    invocation.meth = ident;
    @SuppressWarnings("unchecked")
    var element = (TreeElement<JCTree.JCMethodInvocation>) mock(TreeElement.class);
    doReturn(invocation).when(element).getElement();
    var imports = mock(MacrosImportsHandler.class);
    doReturn(false).when(imports).isLiteralFunctionSupported("");
    doReturn(imports).when(element).getImports();

    var handler = MacroHandlerFactory.createHandler(element);

    assertThat(handler).isEmpty();
  }
}
