package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import javax.script.*;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavaScriptHandler extends InitMacroHandler {
  private Object replacement = null;

  protected JavaScriptHandler(TreeElement<JCTree.JCIdent> identifier) {
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
  JCTree.JCExpression getReplacement(TreeElement<? extends JCTree> identifier) {
    return identifier.getBuilder().createLiteral(replacement);
  }

  @SuppressWarnings("removal")
  private static Object compileAndExecuteScript(String script) throws Exception {
    System.setProperty("nashorn.args", "--no-deprecation-warning");
    var engine = new ScriptEngineManager().getEngineByMimeType("text/javascript");
    CompiledScript compiled = ((Compilable) engine).compile(script);
    var eval = compiled.eval();
    if ((eval instanceof String)
        || (eval instanceof Boolean)
        || (eval instanceof Number)
        || (eval == null)) {
      return eval;
    } else if (eval instanceof ScriptObjectMirror) {
      var json = (ScriptObjectMirror) engine.eval("JSON");
      return json.callMember("stringify", eval);
    } else {
      throw new Exception("Can not change " + eval + " to literal");
    }
  }
}
