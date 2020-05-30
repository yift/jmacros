package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
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
      var invoker = new Invoker(clazz);
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
}
