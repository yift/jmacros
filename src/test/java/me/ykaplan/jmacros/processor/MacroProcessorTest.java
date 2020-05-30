package me.ykaplan.jmacros.processor;

import static org.mockito.Mockito.*;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import org.junit.jupiter.api.Test;

public class MacroProcessorTest {
  @Test
  public void initWithoutJavaCEnv() {
    var env = mock(ProcessingEnvironment.class);
    var messager = mock(Messager.class);
    doReturn(messager).when(env).getMessager();

    var macroProcessor = new MacroProcessor();

    macroProcessor.init(env);

    verify(messager).printMessage(eq(Diagnostic.Kind.ERROR), anyString());
  }
}
