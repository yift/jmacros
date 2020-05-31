package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import me.ykaplan.jmacros.LiteralMacro;
import me.ykaplan.jmacros.Macro;

class MacroExtractor implements UnitProcessable {
  @Override
  public void processUnit(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
    var unit = new Unit(compilationUnitTree);
    unit.extract();
  }

  private static java.util.List<Parameter> extractParameters(
      TreeElement<JCTree.JCMethodDecl> method) {
    var originalParameters = method.getElement().getParameters();
    var parameters =
        originalParameters.stream()
            .map(Parameter::create)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    if (originalParameters.size() != parameters.size()) {
      method.error("Only support for String arguments");
      return null;
    }
    return parameters;
  }

  private static class Parameter {
    private final String name;
    private final boolean isArray;

    private Parameter(String name, boolean isArray) {
      this.name = name;
      this.isArray = isArray;
    }

    public String toDeclaration() {
      return "String " + (isArray ? "..." : "") + name;
    }

    public static Parameter create(JCTree.JCVariableDecl variable) {
      var name = variable.name.toString();
      JCTree.JCIdent type = null;
      boolean isArray = false;
      if (variable.vartype instanceof JCTree.JCIdent) {
        type = (JCTree.JCIdent) variable.vartype;
      } else if (variable.vartype instanceof JCTree.JCArrayTypeTree) {
        var array = (JCTree.JCArrayTypeTree) variable.vartype;
        isArray = true;
        if (array.elemtype instanceof JCTree.JCIdent) {
          type = (JCTree.JCIdent) array.elemtype;
        }
      }

      if (type == null) {
        return null;
      }
      if (!type.name.toString().equals("String")) {
        return null;
      }
      return new Parameter(name, isArray);
    }
  }

  private static class Method {
    private final String name;
    private final java.util.List<Parameter> parameters;
    private final TreeElement<JCTree.JCMethodDecl> method;
    private final boolean returnLiteral;

    private Method(
        String name,
        java.util.List<Parameter> parameters,
        TreeElement<JCTree.JCMethodDecl> method,
        boolean returnLiteral) {
      this.name = name;
      this.parameters = parameters;
      this.method = method;
      this.returnLiteral = returnLiteral;
    }

    public static Method extractMethod(TreeElement<JCTree.JCMethodDecl> method) {
      if (!(method.getElement().getReturnType() instanceof JCTree.JCIdent)) {
        return null;
      }
      var returnType = (JCTree.JCIdent) method.getElement().getReturnType();
      if (returnType.sym == null) {
        return null;
      }
      boolean returnLiteral;
      if (returnType.sym.toString().equals(Macro.class.getCanonicalName())) {
        returnLiteral = false;
      } else if (returnType.sym.toString().equals(LiteralMacro.class.getCanonicalName())) {
        returnLiteral = true;
      } else {
        return null;
      }

      var parent = method.getParent();
      if (!(parent.getElement() instanceof JCTree.JCClassDecl)) {
        method.error("Macro should be defined within a class");
      }
      var classDeclaration = (JCTree.JCClassDecl) parent.getElement();
      classDeclaration.defs = List.filter(classDeclaration.defs, method.getElement());

      var modifiers = method.getElement().mods.flags;
      if ((modifiers & Modifier.PRIVATE) != Modifier.PRIVATE) {
        method.error("Macro function must be private and used only within the class");
        return null;
      }
      if ((modifiers & Modifier.STATIC) != Modifier.STATIC) {
        method.error("Macro function must be static and can not have state");
        return null;
      }

      var parameters = extractParameters(method);
      if (parameters == null) {
        return null;
      }

      return new Method(method.getElement().name.toString(), parameters, method, returnLiteral);
    }

    String methodAsString() {
      var block = CodeCreator.getCode(method.getElement().body, method);
      var code =
          "public static "
              + (returnLiteral ? "Object" : "String")
              + " "
              + method.getElement().getName()
              + "("
              + parameters.stream().map(Parameter::toDeclaration).collect(Collectors.joining(","))
              + ") throws Exception "
              + block;
      return code;
    }
  }

  private class Unit {
    private final Map<String, Method> methods = new HashMap<>();
    private final Optional<Invoker> invoker;
    private final TreeElement<JCTree.JCCompilationUnit> compilationUnitTree;

    Unit(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
      this.compilationUnitTree = compilationUnitTree;
      compilationUnitTree.forEachOfType(
          JCTree.JCMethodDecl.class,
          m -> {
            var method = Method.extractMethod(m);
            if (method != null) {
              var otherMethod = methods.put(method.name, method);
              if (otherMethod != null) {
                m.error("Duplicate macro: '" + method.name + "'");
                otherMethod.method.warning("Duplicate macro: '" + method.name + "'");
                methods.clear();
                return;
              }
            }
          });

      if (methods.isEmpty()) {
        invoker = Optional.empty();
        return;
      }
      var classCode =
          "\n"
              + "public class Macros {\n"
              + methods.values().stream()
                  .map(Method::methodAsString)
                  .collect(Collectors.joining("\n"))
              + "\n}";
      var compiler = new Compiler(compilationUnitTree, classCode, "Macros");
      invoker = compiler.createInvoker();
    }

    void extract() {
      if (invoker.isEmpty()) {
        // Nothing to do.
        return;
      }
      compilationUnitTree.forEachOfType(JCTree.JCMethodInvocation.class, this::replaceMethod);
    }

    private <R extends JCTree> void replaceMethod(
        TreeElement<JCTree.JCMethodInvocation> methodInvocation) {
      var expression = methodInvocation.getElement().meth;
      Method method;
      if (expression instanceof JCTree.JCFieldAccess) {
        var fieldAccess = (JCTree.JCFieldAccess) expression;
        method = methods.get(fieldAccess.name.toString());
        if (method == null) {
          return;
        }
        if (fieldAccess.selected instanceof JCTree.JCIdent) {
          var selected = (JCTree.JCIdent) fieldAccess.selected;
          if ((selected.sym != null)
              || (selected.type != null)
              || (!selected.name.toString().equals(method.method.getClassName()))) {
            return;
          }
        } else {
          return;
        }
      } else if (expression instanceof JCTree.JCIdent) {
        var ident = (JCTree.JCIdent) expression;
        method = methods.get(ident.name.toString());
        if (method == null) {
          return;
        }
      } else {
        return;
      }

      var args =
          methodInvocation.getElement().args.stream()
              .map(Object::toString)
              .collect(Collectors.toList());
      try {
        var replacement = invoker.get().invoke(method.name, args);
        JCTree.JCExpression newExpression;
        if (method.returnLiteral) {
          if ((replacement instanceof String)
              || (replacement instanceof Number)
              || (replacement instanceof Boolean)
              || (replacement == null)) {
            newExpression = methodInvocation.getBuilder().createLiteral(replacement);
          } else {
            var message =
                "Literal Macro must return number, Boolean, string, not "
                    + replacement.getClass().getCanonicalName();
            method.method.error(message);
            methodInvocation.warning(message);
            return;
          }
        } else {
          if (!(replacement instanceof String)) {
            var message = "Macro must return the expression string not " + replacement;
            method.method.error(message);
            methodInvocation.warning(message);
            return;
          }
          var compiler = new SubTreeParser(replacement.toString(), methodInvocation);
          newExpression = compiler.parse();
        }
        ExpressionReplacer.replace(methodInvocation, newExpression);
      } catch (Exception e) {
        var message = e.getMessage();
        if ((message == null) && (e.getCause() != null)) {
          message = e.getCause().getMessage();
        }
        methodInvocation.error("Macro error: " + message);
      }
    }
  }
}
