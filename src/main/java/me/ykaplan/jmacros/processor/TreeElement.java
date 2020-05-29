package me.ykaplan.jmacros.processor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

class TreeElement<T extends JCTree> {
  private final T element;
  private final TreeElement<?> parent;
  private final Trees trees;
  private final TreeBuilderFactory builder;

  static TreeElement<JCTree.JCCompilationUnit> getUnit(
      Element element, JavacProcessingEnvironment processingEnvironment) {
    var builder = new TreeBuilderFactory(processingEnvironment);
    var trees = Trees.instance(processingEnvironment);
    try {
      var tree = trees.getPath(element);
      if (tree != null) {
        var compilationUnit = tree.getCompilationUnit();
        if (compilationUnit instanceof JCTree.JCCompilationUnit) {
          return new TreeElement<>(
              (JCTree.JCCompilationUnit) compilationUnit, null, trees, builder);
        }
      }
    } catch (Exception e) {
      // Do nothing.
    }
    return null;
  }

  private TreeElement(T element, TreeElement<?> parent) {
    this(element, parent, parent.trees, parent.builder);
  }

  private TreeElement(T element, TreeElement<?> parent, Trees trees, TreeBuilderFactory builder) {
    this.element = element;
    this.parent = parent;
    this.trees = trees;
    this.builder = builder;
  }

  T getElement() {
    return element;
  }

  TreeElement<?> getParent() {
    return parent;
  }

  void warning(String text) {
    message(Diagnostic.Kind.WARNING, text);
  }

  void error(String text) {
    message(Diagnostic.Kind.ERROR, text);
  }

  <R extends JCTree> void forEachOfType(Class<R> type, Consumer<TreeElement<R>> consumer) {
    var stack = new Stack<TreeElement<?>>();
    stack.push(this);
    new TreeScanner<>() {
      @Override
      public Object scan(Tree tree, Object o) {
        if (tree instanceof JCTree) {
          var item = (JCTree) tree;
          if (item != stack.peek().element) {
            var element = new TreeElement<>(item, stack.peek());
            if (type.isAssignableFrom(tree.getClass())) {
              @SuppressWarnings("unchecked")
              var elementInCorrectType = (TreeElement<R>) element;
              consumer.accept(elementInCorrectType);
            }
            stack.push(element);
            super.scan(tree, o);
            stack.pop();
          } else {
            super.scan(tree, o);
          }
        }
        return null;
      }
    }.scan(element, null);
  }

  CompilationUnitTree getUnit() {
    if (element instanceof CompilationUnitTree) {
      return (CompilationUnitTree) element;
    }
    if (parent == null) {
      return null;
    }
    return getParent().getUnit();
  }

  long getLineNumber() {
    return getUnit().getLineMap().getLineNumber(element.getStartPosition());
  }

  String getMethodName() {
    if (element instanceof JCTree.JCMethodDecl) {
      return ((JCTree.JCMethodDecl) element).getName().toString();
    }
    if (parent == null) {
      return "<>";
    }
    return parent.getMethodName();
  }

  String getFileName() {
    return getUnit().getSourceFile().getName();
  }

  private void message(Diagnostic.Kind kind, String text) {
    trees.printMessage(kind, text, element, getUnit());
  }

  @SuppressWarnings("unchecked")
  TreeElement<JCTree.JCClassDecl> getClassType() {
    if (parent == null) {
      return null;
    }
    if (element instanceof JCTree.JCClassDecl) {
      return (TreeElement<JCTree.JCClassDecl>) this;
    }
    return parent.getClassType();
  }

  String getClassName() {
    if (parent == null) {
      return "";
    }
    var className = parent.getClassName();
    if (element instanceof JCTree.JCClassDecl) {
      if (!className.isEmpty()) {
        className = className + ".";
      }
      className = className + ((JCTree.JCClassDecl) element).getSimpleName();
    }
    return className;
  }

  TreeBuilder getBuilder() {
    return builder.builder(element);
  }

  private static String debugPrint(Object obj) {
    if (obj instanceof JCTree) {
      var builder = new StringBuilder("{");
      builder.append("\"ClassName\": \"").append(obj.getClass().getSimpleName()).append('"');
      for (var field : obj.getClass().getFields()) {
        try {
          builder
              .append(",\"")
              .append(field.getName())
              .append("\":")
              .append(debugPrint(field.get(obj)));
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
      return builder.append("}").toString();

    } else if (obj instanceof List) {
      return ((List<? extends Object>) obj)
          .stream().map(TreeElement::debugPrint).collect(Collectors.joining(",", "[", "]"));
    } else if ((obj instanceof Number) || (obj instanceof Boolean)) {
      return obj.toString();
    } else if (obj != null) {
      return "\"" + obj.toString() + "\"";
    } else {
      return "null";
    }
  }

  @Override
  public String toString() {
    return "Element: " + debugPrint(element);
  }
}
