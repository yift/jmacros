package me.ykaplan.jmacros.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.util.List;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("*")
public class MacroProcessor extends AbstractProcessor {
  private JavacProcessingEnvironment javaProcessingEnvironment;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    if (!(processingEnvironment instanceof JavacProcessingEnvironment)) {
      processingEnvironment
          .getMessager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              "Can not cast processingEnvironment to JavacProcessingEnvironment!");
    } else {
      javaProcessingEnvironment = (JavacProcessingEnvironment) processingEnvironment;
    }
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
    var imports = new MacrosImportsHandler(compilationUnitTree);
    if (!imports.anyMacroSupporter()) {
      // Nothing to do
      return;
    }
    compilationUnitTree.getElement().defs = imports.newDefs();
    var macroHandleFactory = new MacroHandlerFactory(imports);
    compilationUnitTree.forEachOfType(
        JCTree.JCVariableDecl.class, variable -> processVariable(variable, macroHandleFactory));
  }

  private void processVariable(
      TreeElement<JCTree.JCVariableDecl> variable, MacroHandlerFactory macroHandlerFactory) {
    var handler = macroHandlerFactory.createHandler(variable);
    if (handler.isEmpty()) {
      // Nothing to do.
      return;
    }

    if (!setModifiers(variable)) {
      return;
    }

    var name = variable.getElement().getName();
    variable
        .getParent()
        .forEachOfType(
            JCTree.JCIdent.class,
            identifier -> {
              if (identifier.getElement().getName().equals(name)) {
                replace(identifier, handler.get().getReplacement(identifier));
              }
            });

    removeVariable(variable);
  }

  private void removeVariable(TreeElement<JCTree.JCVariableDecl> variable) {
    var parent = variable.getParent().getElement();
    var parentKind = parent.getKind();
    if (parentKind == Tree.Kind.BLOCK) {
      var block = (JCTree.JCBlock) parent;
      block.stats = List.filter(block.stats, variable.getElement());
    } else if (parentKind == Tree.Kind.CLASS) {
      var classDecl = (JCTree.JCClassDecl) parent;
      classDecl.defs = List.filter(classDecl.defs, variable.getElement());
    }
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

  private void replace(TreeElement<JCTree.JCIdent> identifier, JCTree.JCExpression replacement) {
    var parent = identifier.getParent();
    boolean set = false;
    if (parent.getElement() instanceof JCTree.JCVariableDecl) {
      var declaration = (JCTree.JCVariableDecl) parent.getElement();
      if (declaration.init == identifier.getElement()) {
        declaration.init = replacement;
        set = true;
      }
    } else if (parent.getElement() instanceof JCTree.JCBinary) {
      var binary = (JCTree.JCBinary) parent.getElement();
      if (binary.lhs == identifier.getElement()) {
        binary.lhs = replacement;
        set = true;
      }
      if (binary.rhs == identifier.getElement()) {
        binary.rhs = replacement;
        set = true;
      }
    } else if (parent.getElement() instanceof JCTree.JCMethodInvocation) {
      var methodInvocation = (JCTree.JCMethodInvocation) parent.getElement();
      List<JCTree.JCExpression> newArgs = List.nil();
      for (var expression : methodInvocation.args) {
        if (expression == identifier.getElement()) {
          newArgs = newArgs.append(replacement);
          set = true;
        } else {
          newArgs = newArgs.append(expression);
        }
      }
      methodInvocation.args = newArgs;
    } else if (parent.getElement() instanceof JCTree.JCFieldAccess) {
      var fieldAccess = (JCTree.JCFieldAccess) parent.getElement();
      if (fieldAccess.selected == identifier.getElement()) {
        fieldAccess.selected = replacement;
        set = true;
      }
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
    return TreeElement.getUnit(element, javaProcessingEnvironment);
  }
}
