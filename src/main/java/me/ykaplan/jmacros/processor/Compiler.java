package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

class Compiler extends InternalCompiler {
  private final TreeElement<? extends JCTree> element;

  Compiler(TreeElement<? extends JCTree> element, String code, String className) {
    super(code, className);
    this.element = element;
  }

  public Optional<Invoker> createInvoker() {
    if (!compile()) {
      return Optional.empty();
    }
    try {
      var classLoader =
          new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) {
              var bytes = getRawClass(name);
              return defineClass(name, bytes, 0, bytes.length);
            }
          };
      var clazz = classLoader.loadClass(className);
      var invoker =
          new Invoker() {
            @Override
            public Object invoke(String methodName, List<String> args) throws Exception {
              Method method = null;
              for (var m : clazz.getDeclaredMethods()) {
                if ((m.getName().equals(methodName)) && (m.getParameterCount() == args.size())) {
                  method = m;
                  break;
                }
              }
              if (method == null) {
                for (var m : clazz.getDeclaredMethods()) {
                  var types = m.getParameterTypes();
                  if ((m.getName().equals(methodName))
                      && (types.length - 1 <= args.size())
                      && (types.length > 0)
                      && (types[types.length - 1].isArray())) {
                    method = m;
                    break;
                  }
                }
              }
              if (method == null) {
                throw new Exception("Could not find method: " + methodName);
              }
              var types = method.getParameterTypes();
              Object[] argsToSend;
              if ((types.length > 0) && (types[types.length - 1].isArray())) {
                argsToSend = new Object[types.length];
                for (int i = 0; i < types.length - 1; ++i) {
                  argsToSend[i] = args.get(i);
                }
                var lastArg = new String[args.size() - types.length + 1];
                for (int i = types.length - 1; i < args.size(); ++i) {
                  lastArg[i - types.length + 1] = args.get(i);
                }
                argsToSend[types.length - 1] = lastArg;
              } else {
                argsToSend = args.toArray();
              }

              return method.invoke(null, argsToSend);
            }
          };
      return Optional.of(invoker);
    } catch (Exception e) {
      gotError("Error: " + e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public void gotError(String error) {
    element.error(error);
  }

  @Override
  public void gotWarning(String warning) {
    element.warning(warning);
  }

  interface Invoker {
    Object invoke(String methodName, List<String> args) throws Exception;
  }
}
