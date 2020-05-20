package me.ykaplan.jmacros.processor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.*;
import javax.tools.Diagnostic;
import me.ykaplan.jmacros.Macro;

@SupportedAnnotationTypes("*")
public class MacroProcessor extends AbstractProcessor {
  private Trees trees;
  private TreeMaker treeMaker;
  private Elements elements;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    trees = Trees.instance(processingEnvironment);
    if (!(processingEnvironment instanceof JavacProcessingEnvironment)) {
      processingEnvironment
          .getMessager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              "Can not cast processingEnvironment to JavacProcessingEnvironment!");
    } else {
      var context = ((JavacProcessingEnvironment) processingEnvironment).getContext();
      treeMaker = TreeMaker.instance(context);
    }
    elements = processingEnvironment.getElementUtils();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    roundEnv.getRootElements().stream()
        .map(this::getUnit)
        .filter(Objects::nonNull)
        .forEach(this::processUnit);

    return false;
  }

  private void processUnit(TreeElement<JCCompilationUnit> compilationUnitTree) {
    boolean hasImportForMacro =
        compilationUnitTree.element.defs.stream()
            .filter(tree -> tree instanceof JCTree.JCImport)
            .map(tree -> (JCTree.JCImport) tree)
            .map(JCTree.JCImport::getQualifiedIdentifier)
            .filter(def -> def instanceof JCTree.JCFieldAccess)
            .map(def -> (JCTree.JCFieldAccess) def)
            .anyMatch(def -> def.toString().equals(Macro.class.getCanonicalName()));
    if (hasImportForMacro) {
      compilationUnitTree.forEachOfType(JCTree.JCVariableDecl.class, this::processVariable);
    }
  }

  private void processVariable(TreeElement<JCTree.JCVariableDecl> variable) {
    var tree = variable.getElement();
    if ((!(tree.getType() instanceof JCTree.JCIdent))
        || (!((JCTree.JCIdent) tree.getType())
            .getName()
            .toString()
            .equals(Macro.class.getSimpleName()))) {
      // Not a macro, no point to continue
      return;
    }

    if (!setModifiers(variable)) {
      return;
    }

    var rawValue = getValue(variable);

    if (rawValue == null) {
      return;
    }

    variable
        .getParent()
        .forEachOfType(
            JCTree.JCIdent.class,
            identifier -> {
              if (identifier.element.name.equals(tree.getName())) {
                replace(identifier, rawValue);
              }
            });

    Name name = (Name) elements.getName("String");
    tree.vartype = treeMaker.Ident(name);
  }

  private String getValue(TreeElement<JCTree.JCVariableDecl> variable) {
    var init = variable.getElement().init;
    var name = variable.getElement().getName();
    if (init == null) {
      variable.error("Macro " + name + " must have initialization");
      return null;
    } else {
      if ((init.getKind() != Tree.Kind.STRING_LITERAL) || (!(init instanceof JCTree.JCLiteral))) {
        variable.error("Macro " + name + " must have string initialization");
        return null;
      }
    }

    var initElement = (JCTree.JCLiteral) init;
    if (!(initElement.value instanceof String)) {
      variable.error("Macro " + name + " must have string initialization");
      return null;
    }
    return initElement.value.toString();
  }

  private boolean setModifiers(TreeElement<JCTree.JCVariableDecl> variable) {
    var parentKind = variable.getParent().getElement().getKind();
    var mods = variable.getElement().mods;
    var name = variable.getElement().getName();
    if (parentKind == Tree.Kind.METHOD) {
      variable.error("Macro " + name + " can not be an argument");
      return false;
    } else if (parentKind == Tree.Kind.BLOCK) {
      if ((mods.flags & Modifier.FINAL) == 0) {
        variable.warning("Macro " + name + " variable is not final");
        mods.flags &= Modifier.FINAL;
      }
    } else if (parentKind == Tree.Kind.CLASS) {
      if ((mods.flags & Modifier.FINAL) == 0) {
        variable.warning("Macro " + name + " member is not final");
        mods.flags &= Modifier.FINAL;
      }
      if ((mods.flags & Modifier.PRIVATE) == 0) {
        variable.warning("Macro " + name + " member is not private");
        mods.flags &= Modifier.PRIVATE;
      }
      if ((mods.flags & Modifier.STATIC) == 0) {
        variable.warning("Macro " + name + " member is not static");
        mods.flags &= Modifier.STATIC;
      }
    } else {
      variable.error("Macro " + name + " - macro support only for members and variables");
      return false;
    }

    return true;
  }

  private String createReplacementString(TreeElement<JCTree.JCIdent> identifier, String rawValue) {
    return rawValue
        .replace("__LINE__", Long.toString(identifier.getLineNumber()))
        .replace("__METHOD__", identifier.getMethodName())
        .replace("__FILE__", identifier.getFileName())
        .replace("__CLASS__", identifier.getClassName());
  }

  private void replace(TreeElement<JCTree.JCIdent> identifier, String rawValue) {
    var parent = identifier.parent;
    var newValue = createReplacementString(identifier, rawValue);
    var literal = treeMaker.Literal(newValue);
    boolean set = false;
    if (parent.element instanceof JCTree.JCVariableDecl) {
      var declaration = (JCTree.JCVariableDecl) parent.element;
      if (declaration.init == identifier.element) {
        declaration.init = literal;
        set = true;
      }
    } else if (parent.element instanceof JCTree.JCBinary) {
      var binary = (JCTree.JCBinary) parent.element;
      if (binary.lhs == identifier.element) {
        binary.lhs = literal;
        set = true;
      }
      if (binary.rhs == identifier.element) {
        binary.rhs = literal;
        set = true;
      }
    } else if (parent.element instanceof JCTree.JCMethodInvocation) {
      var methodInvocation = (JCTree.JCMethodInvocation) parent.element;
      List<JCTree.JCExpression> newArgs = List.nil();
      for (var expression : methodInvocation.args) {
        if (expression == identifier.element) {
          newArgs = newArgs.append(literal);
          set = true;
        } else {
          newArgs = newArgs.append(expression);
        }
      }
      methodInvocation.args = newArgs;
    }
    if (!set) {
      identifier.error("Can not use macro");
    }
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private TreeElement<JCCompilationUnit> getUnit(Element element) {
    try {
      var tree = trees.getPath(element);
      if (tree != null) {
        var compilationUnit = tree.getCompilationUnit();
        if (compilationUnit instanceof JCCompilationUnit) {
          return new TreeElement<>((JCCompilationUnit) compilationUnit);
        }
      }
    } catch (Exception e) {
      // Do nothing.
    }
    return null;
  }

  private class TreeElement<T extends JCTree> {
    private final T element;
    private final TreeElement<?> parent;

    private TreeElement(T element) {
      this(element, null);
    }

    private TreeElement(T element, TreeElement<?> parent) {
      this.element = element;
      this.parent = parent;
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
  }
}
