package me.ykaplan.jmacros.processor;

import java.lang.reflect.Method;
import java.util.List;

class Invoker {
  private final Class<?> clazz;

  Invoker(Class<?> clazz) {
    this.clazz = clazz;
  }

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
}
