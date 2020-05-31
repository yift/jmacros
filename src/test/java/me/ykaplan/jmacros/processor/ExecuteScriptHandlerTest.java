package me.ykaplan.jmacros.processor;

import static org.mockito.Mockito.*;

import com.sun.tools.javac.tree.JCTree;
import org.junit.jupiter.api.Test;

public class ExecuteScriptHandlerTest {
  @Test
  public void timeoutInExecute() throws Exception {
    var command = "test";
    var runtime = mock(Runtime.class);
    var process = mock(Process.class);
    doReturn(process).when(runtime).exec(command);
    @SuppressWarnings("unchecked")
    var element = (TreeElement<JCTree.JCMethodInvocation>) mock(TreeElement.class);
    var handler = new ExecuteScriptHandler(element, runtime);
    doReturn(false).when(process).waitFor(anyLong(), any());

    handler.validateAndKeepArgument("test");

    verify(element).error(anyString());
  }
}
