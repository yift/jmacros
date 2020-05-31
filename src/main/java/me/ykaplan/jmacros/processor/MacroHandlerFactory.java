package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.ykaplan.jmacros.LiteralMacro;

class MacroHandlerFactory {
  private static final Set<Class<? extends MacroHandler>> handlers =
      Set.of(
          LineNumberHandler.class,
          FileNameHandler.class,
          MethodNameHandler.class,
          ClassNameHandler.class,
          BuildTimeHandler.class,
          BuildEnvironmentVariableHandler.class,
          JavaScriptHandler.class,
          FileContentAsStringHandler.class,
          FileContentAsBytesHandler.class,
          ExecuteScriptHandler.class,
          UrlContentAsBytesHandler.class,
          UrlContentAsStringHandler.class,
          ClassTypeHandler.class);

  public static Optional<? extends MacroHandler> createHandler(
      TreeElement<JCTree.JCMethodInvocation> invocation) {
    var methodName = extractMethodName(invocation);
    if (methodName.isEmpty()) {
      return Optional.empty();
    }
    var name = methodName.get();
    return handlers.stream()
        .filter(cls -> classHandlerCanHandleFunction(cls, name))
        .map(
            cls -> {
              try {
                var constructor = cls.getDeclaredConstructor(TreeElement.class);
                return constructor.newInstance(invocation);
              } catch (Exception e) {
                invocation.error("Could not create handler for " + name + " - " + e.getMessage());
                return null;
              }
            })
        .filter(Objects::nonNull)
        .filter(MacroHandler::validate)
        .findAny();
  }

  private static Optional<String> extractMethodName(
      TreeElement<JCTree.JCMethodInvocation> invocation) {
    var methodInvocation = invocation.getElement();
    var method = methodInvocation.meth;
    return getMethodName(method)
        .flatMap(
            methodName -> {
              var methodPath = methodName.split("\\.");
              if (methodPath.length == 0) {
                return Optional.empty();
              }
              if (methodPath.length == 1) {
                if (!invocation.getImports().isLiteralFunctionSupported(methodPath[0])) {
                  return Optional.empty();
                }
                return Optional.of(methodName);
              }
              if (methodPath.length == 2) {
                if ((methodPath[0].equals(LiteralMacro.class.getSimpleName()))
                    && (invocation.getImports().isMacroSupported(LiteralMacro.class))) {
                  return Optional.of(methodPath[1]);
                } else {
                  return Optional.empty();
                }
              }
              if (methodName.startsWith(LiteralMacro.class.getCanonicalName())) {
                return Optional.of(methodPath[methodPath.length - 1]);
              }

              return Optional.empty();
            });
  }

  private static Optional<String> getMethodName(JCTree.JCExpression expression) {
    if (expression instanceof JCTree.JCFieldAccess) {
      var access = (JCTree.JCFieldAccess) expression;
      return getMethodName(access.selected).map(s -> s + "." + access.name.toString());
    } else if (expression instanceof JCTree.JCIdent) {
      return Optional.of(((JCTree.JCIdent) expression).name.toString());
    } else {
      return Optional.empty();
    }
  }

  private static boolean classHandlerCanHandleFunction(
      Class<? extends MacroHandler> clazz, String functionName) {
    var className = clazz.getSimpleName();
    var expectedName = className.substring(0, className.length() - "Handler".length());
    var capitalizedFunctionName =
        functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
    return expectedName.equals(capitalizedFunctionName);
  }
}
