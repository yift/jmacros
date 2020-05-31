package me.ykaplan.jmacros;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class LiteralMacro {

  public static String buildEnvironmentVariable(String key) {
    return Optional.ofNullable(System.getenv(key)).orElse("");
  }

  private static Instant now = Instant.now();

  public static Instant buildTime() {
    return now;
  }

  public static Class<?> classType() {
    try {
      return Class.forName(new Exception().getStackTrace()[1].getClassName());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static String className() {
    return classType().getSimpleName();
  }

  public static String executeScript(String command) {
    try {
      var process = Runtime.getRuntime().exec(command);
      if (!process.waitFor(30, TimeUnit.SECONDS)) {
        throw new Exception("Timeout!");
      }
      var error = new String(process.getErrorStream().readAllBytes());
      if (!error.isBlank()) {
        throw new Exception("Got error:" + error);
      }
      if (process.exitValue() != 0) {
        throw new Exception("Execution failed");
      }
      return new String(process.getInputStream().readAllBytes());

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] fileContentAsBytes(String filePath) {
    try {
      var path = Paths.get(filePath);
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String fileContentAsString(String filePath) {
    try {
      var path = Paths.get(filePath);
      return Files.readString(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String fileName() {
    return new Exception().getStackTrace()[1].getFileName();
  }

  @SuppressWarnings("removal")
  public static Object javaScript(String script) {
    try {
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
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static int lineNumber() {
    return new Exception().getStackTrace()[1].getLineNumber();
  }

  public static String methodName() {
    return new Exception().getStackTrace()[1].getMethodName();
  }

  public static byte[] urlContentAsByte(String urlAsString) {
    try {
      var url = new URL(urlAsString);
      try (var reader = url.openStream(); ) {
        return reader.readAllBytes();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String urlContentAsString(String urlAsString) {
    return new String(urlContentAsByte(urlAsString));
  }
}
