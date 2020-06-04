package me.ykaplan.jmacros.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.lang.model.element.Element;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

public class TreeElementTest {
  @Test
  @SuppressWarnings("unchecked")
  public void getUnitGotException() {
    var element = mock(Element.class);
    var env = mock(JavacProcessingEnvironment.class);
    var maker = mock(TreeMaker.class);
    var names = mock(Names.class);
    var trees = mock(JavacTrees.class);
    var contextGets = List.of(maker, names, trees);
    var index = new AtomicInteger(0);
    var context =
        mock(
            Context.class,
            (Answer<?>)
                invocation -> {
                  if (invocation.getMethod().getName().equals("get")) {
                    var ret = contextGets.get(index.getAndIncrement());
                    return ret;
                  }
                  return null;
                });
    doReturn(context).when(env).getContext();
    doThrow(new RuntimeException()).when(trees).getPath(any());

    var unit = TreeElement.getUnit(element, env);

    assertThat(unit).isNull();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getUnitNullTree() {
    var element = mock(Element.class);
    var env = mock(JavacProcessingEnvironment.class);
    var maker = mock(TreeMaker.class);
    var names = mock(Names.class);
    var trees = mock(JavacTrees.class);
    var contextGets = List.of(maker, names, trees);
    var index = new AtomicInteger(0);
    var context =
        mock(
            Context.class,
            (Answer<?>)
                invocation -> {
                  if (invocation.getMethod().getName().equals("get")) {
                    var ret = contextGets.get(index.getAndIncrement());
                    return ret;
                  }
                  return null;
                });
    doReturn(context).when(env).getContext();
    doReturn(null).when(trees).getPath(any());

    var unit = TreeElement.getUnit(element, env);

    assertThat(unit).isNull();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getUnitNotJcCompilationUnit() {
    var element = mock(Element.class);
    var env = mock(JavacProcessingEnvironment.class);
    var maker = mock(TreeMaker.class);
    var names = mock(Names.class);
    var trees = mock(JavacTrees.class);
    var contextGets = List.of(maker, names, trees);
    var index = new AtomicInteger(0);
    var context =
        mock(
            Context.class,
            (Answer<?>)
                invocation -> {
                  if (invocation.getMethod().getName().equals("get")) {
                    var ret = contextGets.get(index.getAndIncrement());
                    return ret;
                  }
                  return null;
                });
    doReturn(context).when(env).getContext();
    var path = mock(TreePath.class);
    doReturn(path).when(trees).getPath(any());

    var unit = TreeElement.getUnit(element, env);

    assertThat(unit).isNull();
  }
}
