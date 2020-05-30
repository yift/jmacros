package me.ykaplan.jmacros.processor;

import static org.mockito.Mockito.*;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.Test;

public class SubTreeParserTest {
  @Test
  public void warningBeforeParse() {
    var element = mock(TreeElement.class);
    SubTreeParser parser = new SubTreeParser("1", element);
    var warning = "warn";

    parser.gotWarning(warning);

    verify(element).warning(warning);
  }

  @Test
  public void warningAfterParse() {
    var element = mock(TreeElement.class);
    var tree = mock(JCTree.class);
    tree.pos = 1;
    doReturn(tree).when(element).getElement();
    SubTreeParser parser = new SubTreeParser("1", element);
    var warning = "warn";
    parser.parse();

    parser.gotWarning(warning);

    verify(element, never()).warning(anyString());
  }

  @Test
  public void tooFewDefs() {
    var element = mock(TreeElement.class);
    SubTreeParser parser = new SubTreeParser("1", element);
    var unit = mock(JCTree.JCCompilationUnit.class);
    unit.defs = List.nil();

    parser.parsed(unit);

    verify(element).error(anyString());
  }

  @Test
  public void wrongDefinition() {
    var element = mock(TreeElement.class);
    SubTreeParser parser = new SubTreeParser("1", element);
    var unit = mock(JCTree.JCCompilationUnit.class);
    var firstDef = mock(JCTree.JCLiteral.class);
    unit.defs = List.nil();
    unit.defs = unit.defs.append(firstDef);

    parser.parsed(unit);

    verify(element).error(anyString());
  }

  @Test
  public void tooFewMethods() {
    var element = mock(TreeElement.class);
    SubTreeParser parser = new SubTreeParser("1", element);
    var unit = mock(JCTree.JCCompilationUnit.class);
    var firstDef = mock(JCTree.JCClassDecl.class);
    unit.defs = List.nil();
    unit.defs = unit.defs.append(firstDef);
    firstDef.defs = List.nil();

    parser.parsed(unit);

    verify(element).error(anyString());
  }

  @Test
  public void notAMethod() {
    var element = mock(TreeElement.class);
    SubTreeParser parser = new SubTreeParser("1", element);
    var unit = mock(JCTree.JCCompilationUnit.class);
    var firstDef = mock(JCTree.JCClassDecl.class);
    unit.defs = List.nil();
    unit.defs = unit.defs.append(firstDef);
    firstDef.defs = List.nil();
    var decl = mock(JCTree.JCVariableDecl.class);
    firstDef.defs = firstDef.defs.append(decl);

    parser.parsed(unit);

    verify(element).error(anyString());
  }

  @Test
  public void tooManyStatements() {
    var element = mock(TreeElement.class);
    SubTreeParser parser = new SubTreeParser("1", element);
    var unit = mock(JCTree.JCCompilationUnit.class);
    var firstDef = mock(JCTree.JCClassDecl.class);
    unit.defs = List.nil();
    unit.defs = unit.defs.append(firstDef);
    firstDef.defs = List.nil();
    var method = mock(JCTree.JCMethodDecl.class);
    firstDef.defs = firstDef.defs.append(method);
    method.body = mock(JCTree.JCBlock.class);
    method.body.stats = List.nil();

    parser.parsed(unit);

    verify(element).error(anyString());
  }

  @Test
  public void notReturnStatement() {
    var element = mock(TreeElement.class);
    SubTreeParser parser = new SubTreeParser("1", element);
    var unit = mock(JCTree.JCCompilationUnit.class);
    var firstDef = mock(JCTree.JCClassDecl.class);
    unit.defs = List.nil();
    unit.defs = unit.defs.append(firstDef);
    firstDef.defs = List.nil();
    var method = mock(JCTree.JCMethodDecl.class);
    firstDef.defs = firstDef.defs.append(method);
    method.body = mock(JCTree.JCBlock.class);
    method.body.stats = List.nil();
    method.body.stats = method.body.stats.append(mock(JCTree.JCDoWhileLoop.class));

    parser.parsed(unit);

    verify(element).error(anyString());
  }
}
