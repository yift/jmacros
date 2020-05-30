package me.ykaplan.jmacros.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.Visitor;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TreeMoverTest {
  private final TreeElement<?> element = mock(TreeElement.class);
  private final TreeBuilder builder = mock(TreeBuilder.class);
  private final Name nameOne = mock(Name.class);
  private final Name nameTwo = mock(Name.class);
  private final int pos = 100;

  @BeforeEach
  public void setUp() {
    doReturn(builder).when(element).getBuilder();
    doReturn(nameOne).when(builder).createName(nameTwo);
    var elementTree = mock(JCTree.class);
    doReturn(elementTree).when(element).getElement();
    elementTree.pos = pos;
  }

  @Test
  public void moveNull() {
    TreeMover.move(null, null);
  }

  @Test
  public void typeParameter() {
    new TestClass<>(JCTree.JCTypeParameter.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCTypeParameter tree) {
        visitor.visitTypeParameter(tree);
      }

      @Override
      Name getName(JCTree.JCTypeParameter tree) {
        return tree.name;
      }

      @Override
      void setName(JCTree.JCTypeParameter tree, Name name) {
        tree.name = name;
      }
    }.test();
  }

  @Test
  public void classDecl() {
    new TestClass<>(JCTree.JCClassDecl.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCClassDecl tree) {
        visitor.visitClassDef(tree);
      }

      @Override
      Name getName(JCTree.JCClassDecl tree) {
        return tree.name;
      }

      @Override
      void setName(JCTree.JCClassDecl tree, Name name) {
        tree.name = name;
      }
    }.test();
  }

  @Test
  public void methodDef() {
    new TestClass<>(JCTree.JCMethodDecl.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCMethodDecl tree) {
        visitor.visitMethodDef(tree);
      }

      @Override
      Name getName(JCTree.JCMethodDecl tree) {
        return tree.name;
      }

      @Override
      void setName(JCTree.JCMethodDecl tree, Name name) {
        tree.name = name;
      }
    }.test();
  }

  @Test
  public void varDef() {
    new TestClass<>(JCTree.JCVariableDecl.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCVariableDecl tree) {
        visitor.visitVarDef(tree);
      }

      @Override
      Name getName(JCTree.JCVariableDecl tree) {
        return tree.name;
      }

      @Override
      void setName(JCTree.JCVariableDecl tree, Name name) {
        tree.name = name;
      }
    }.test();
  }

  @Test
  public void labelled() {
    new TestClass<>(JCTree.JCLabeledStatement.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCLabeledStatement tree) {
        visitor.visitLabelled(tree);
      }

      @Override
      Name getName(JCTree.JCLabeledStatement tree) {
        return tree.label;
      }

      @Override
      void setName(JCTree.JCLabeledStatement tree, Name name) {
        tree.label = name;
      }
    }.test();
  }

  @Test
  public void continueStatement() {
    new TestClass<>(JCTree.JCContinue.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCContinue tree) {
        visitor.visitContinue(tree);
      }

      @Override
      Name getName(JCTree.JCContinue tree) {
        return tree.label;
      }

      @Override
      void setName(JCTree.JCContinue tree, Name name) {
        tree.label = name;
      }
    }.test();
  }

  @Test
  public void breakStatement() {
    new TestClass<>(JCTree.JCBreak.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCBreak tree) {
        visitor.visitBreak(tree);
      }

      @Override
      Name getName(JCTree.JCBreak tree) {
        return tree.label;
      }

      @Override
      void setName(JCTree.JCBreak tree, Name name) {
        tree.label = name;
      }
    }.test();
  }

  @Test
  public void select() {
    new TestClass<>(JCTree.JCFieldAccess.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCFieldAccess tree) {
        visitor.visitSelect(tree);
      }

      @Override
      Name getName(JCTree.JCFieldAccess tree) {
        return tree.name;
      }

      @Override
      void setName(JCTree.JCFieldAccess tree, Name name) {
        tree.name = name;
      }
    }.test();
  }

  @Test
  public void reference() {
    new TestClass<>(JCTree.JCMemberReference.class) {
      @Override
      void doVisit(Visitor visitor, JCTree.JCMemberReference tree) {
        visitor.visitReference(tree);
      }

      @Override
      Name getName(JCTree.JCMemberReference tree) {
        return tree.name;
      }

      @Override
      void setName(JCTree.JCMemberReference tree, Name name) {
        tree.name = name;
      }
    }.test();
  }

  @Test
  public void literalInt() {
    testLiteral("100", TypeTag.INT, 100);
  }

  @Test
  public void literalChar() {
    testLiteral("32", TypeTag.CHAR, 32);
  }

  @Test
  public void literalBool() {
    testLiteral("1", TypeTag.BOOLEAN, 1);
  }

  @Test
  public void literalByte() {
    testLiteral("30", TypeTag.BYTE, 30);
  }

  @Test
  public void literalShort() {
    testLiteral("12", TypeTag.SHORT, 12);
  }

  @Test
  public void literalString() {
    testLiteral("hello", TypeTag.CLASS, "hello");
  }

  @Test
  public void literalLong() {
    testLiteral("3001", TypeTag.LONG, 3001L);
  }

  @Test
  public void literalDouble() {
    testLiteral("30.01", TypeTag.DOUBLE, 30.01);
  }

  @Test
  public void literalFloat() {
    testLiteral("20.01", TypeTag.FLOAT, 20.01f);
  }

  @Test
  public void literalUnknown() {
    var errors = new ArrayList<>();
    doAnswer(
            i -> {
              var error = i.getArgumentAt(0, String.class);
              errors.add(error);
              return null;
            })
        .when(element)
        .error(anyString());

    var literal = mock(JCTree.JCLiteral.class);
    var name = mock(Name.class);
    doReturn("...").when(name).toString();
    literal.value = name;
    literal.typetag = TypeTag.PACKAGE;
    doAnswer(
            i -> {
              var visitor = i.getArgumentAt(0, Visitor.class);
              visitor.visitLiteral(literal);
              return null;
            })
        .when(literal)
        .accept(any());

    TreeMover.move(literal, element);

    assertThat(literal.value).isEqualTo(name);
    assertThat(errors).containsOnly("Could not set literal ... of type PACKAGE");
  }

  private void testLiteral(String value, TypeTag type, Object expectedValue) {
    var literal = mock(JCTree.JCLiteral.class);
    var name = mock(Name.class);
    doReturn(value).when(name).toString();
    literal.value = name;
    literal.typetag = type;
    doAnswer(
            i -> {
              var visitor = i.getArgumentAt(0, Visitor.class);
              visitor.visitLiteral(literal);
              return null;
            })
        .when(literal)
        .accept(any());

    TreeMover.move(literal, element);

    assertThat(literal.value).isEqualTo(expectedValue);
  }

  private abstract class TestClass<T extends JCTree> {
    private final T tree;

    private TestClass(Class<T> cls) {
      tree = mock(cls);
      setName(tree, nameTwo);
      doAnswer(
              i -> {
                var visitor = i.getArgumentAt(0, Visitor.class);
                doVisit(visitor, tree);
                return null;
              })
          .when(tree)
          .accept(any());
    }

    abstract void doVisit(Visitor visitor, T tree);

    abstract Name getName(T tree);

    abstract void setName(T tree, Name name);

    void test() {
      TreeMover.move(tree, element);

      assertThat(getName(tree)).isEqualTo(nameOne);
      assertThat(tree.pos).isEqualTo(pos);
    }
  }
}
