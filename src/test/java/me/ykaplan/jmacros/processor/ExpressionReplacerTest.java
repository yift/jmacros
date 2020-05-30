package me.ykaplan.jmacros.processor;

import static org.mockito.Mockito.*;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class ExpressionReplacerTest {
  @Test
  public void variable() {
    unknownParent(JCTree.JCVariableDecl.class);
  }

  @Test
  public void binarry() {
    unknownParent(JCTree.JCBinary.class);
  }

  @Test
  public void invocation() {
    var parent = mock(JCTree.JCMethodInvocation.class);
    parent.args = List.nil();
    var parentElement = mock(TreeElement.class);
    var toReplace = mock(TreeElement.class);
    var replacement = mock(JCTree.JCExpression.class);
    var element = mock(JCTree.JCExpression.class);
    doReturn(parent).when(parentElement).getElement();
    doReturn(parentElement).when(toReplace).getParent();
    doReturn(element).when(toReplace).getElement();

    ExpressionReplacer.replace(toReplace, replacement);
    verify(toReplace).error(anyString());
  }

  @Test
  public void fieldAccess() {
    unknownParent(JCTree.JCFieldAccess.class);
  }

  @Test
  public void arrayAccess() {
    unknownParent(JCTree.JCArrayAccess.class);
  }

  @Test
  public void returnExpression() {
    unknownParent(JCTree.JCReturn.class);
  }

  @Test
  public void typeCast() {
    unknownParent(JCTree.JCTypeCast.class);
  }

  @Test
  public void assign() {
    unknownParent(JCTree.JCAssign.class);
  }

  @Test
  public void newClass() {
    unknownParent(JCTree.JCNewClass.class);
  }

  @Test
  public void parans() {
    unknownParent(JCTree.JCParens.class);
  }

  @Test
  public void classDef() {
    unknownParent(JCTree.JCClassDecl.class);
  }

  private void unknownParent(Class<? extends JCTree> cls) {
    var parent = mock(cls);
    var parentElement = mock(TreeElement.class);
    var toReplace = mock(TreeElement.class);
    var replacement = mock(JCTree.JCExpression.class);
    var element = mock(JCTree.JCExpression.class);
    doReturn(parent).when(parentElement).getElement();
    doReturn(parentElement).when(toReplace).getParent();
    doReturn(element).when(toReplace).getElement();

    ExpressionReplacer.replace(toReplace, replacement);
    verify(toReplace).error(anyString());
  }
}
