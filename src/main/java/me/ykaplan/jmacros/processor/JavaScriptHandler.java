package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import javax.script.*;

class JavaScriptHandler extends InitMacroHandler {
  private Object replacement = null;

  protected JavaScriptHandler(TreeElement<JCTree.JCMethodInvocation> identifier) {
    super(identifier);
  }

  @Override
  @SuppressWarnings("removal")
  boolean validateAndKeepArgument(Object argument) {
    if (!(argument instanceof String)) {
      error("Argument must be String");
      return false;
    }

    try {
      replacement = compileAndExecuteScript(argument.toString());
      return true;
    } catch (Exception e) {
      error("Can not compile or run JavaScript: " + e.getMessage());
      return false;
    }
  }

  @Override
  JCTree.JCExpression getReplacement(TreeBuilder builder) {
    return builder.createLiteral(replacement);
  }

  @SuppressWarnings("removal")
  private static Object compileAndExecuteScript(String script) throws Exception {
    System.setProperty("nashorn.args", "--no-deprecation-warning");
    try {
      var scriptClass = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");

      var engine = new ScriptEngineManager().getEngineByMimeType("text/javascript");
      CompiledScript compiled = ((Compilable) engine).compile(script);
      var eval = compiled.eval();
      if ((eval instanceof String)
          || (eval instanceof Boolean)
          || (eval instanceof Number)
          || (eval == null)) {
        return eval;
      } else if (scriptClass.isInstance(eval)) {
        var json = engine.eval("JSON");
        var method = scriptClass.getMethod("callMember", String.class, Object[].class);
        Object[] args = {eval};
        return method.invoke(json, "stringify", args);
      } else {
        throw new Exception("Can not change " + eval + " to literal");
      }
    } catch (ClassNotFoundException e) {
      throw new UnsupportedOperationException(
          "Nashorn was removed from the JVM, this feature is no longer availabvle");
    }
  }
}
