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

/**
 * This class can be used to a literal in compile time.
 *
 * <p>To use, declare a static private unique method that returns a LiteralMacro and accept only
 * String arguments. The implementation should return a String, Boolean, Number or Null that is the
 * new value that the compiler will replace the any call for the method, and each of the String
 * argument will be the actual expression.
 *
 * <p>An exception will be translate into a compilation error.
 *
 * <p>For example, to print the host name of the build machine:
 *
 * <pre>
 * import me.ykaplan.jmacros.LiteralMacro;
 *
 * public class Test {
 *
 *   public static void main(String... args) throws Exception {
 *     System.out.println("Build On: " + getHostName());
 *   }
 *
 *   private static LiteralMacro getHostName() throws Exception {
 *     return java.net.InetAddress.getLocalHost().getHostName();
 *   }
 * }
 * </pre>
 *
 * In additional, the static public methods bellow are predefine macro that can be used as is and
 * can also be run from Eclipse.
 *
 * @see Macro
 */
public class LiteralMacro {
  // Note: The implementation of the methods bellow is for IDE use only, the actual implementation
  // will replace the method invocation with the value.

  /**
   * Return an environment variable value (during build time).
   *
   * <p>For example: to print the name of the user that run the compiler use:
   *
   * <pre>
   * import static me.ykaplan.jmacros.LiteralMacro.*;
   *
   * public class Test {
   *
   *   public static void main(String... args) throws Exception {
   *     System.out.println("Build by: " + buildEnvironmentVariable("USER"));
   *   }
   * }
   * </pre>
   *
   * @param key The name of the variable
   * @return The value of the variable or empty string if the variable was not defined during run
   *     time.
   */
  public static String buildEnvironmentVariable(String key) {
    return Optional.ofNullable(System.getenv(key)).orElse("");
  }

  private static Instant now = Instant.now();

  /**
   * Return the time in which the compiler was run.
   *
   * <p>For example:
   *
   * <pre>
   * import java.time.ZoneId;
   * import java.time.format.DateTimeFormatter;
   * import static me.ykaplan.jmacros.LiteralMacro.*;
   *
   * public class Test {
   *
   *   public static void main(String... args) {
   *     var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").
   *       withZone(ZoneId.systemDefault());
   *     System.out.println("Build on: " + formatter.format(buildTime()));
   *   }
   * }
   * </pre>
   *
   * @return an Instant of the build time.
   */
  public static Instant buildTime() {
    return now;
  }

  /**
   * Return the class in which it is called. Unlike getClass can be used in static context as well.
   * So, instead of the <a href="http://www.slf4j.org/manual.html">example from SLF4J</a>:
   *
   * <pre>
   *     Logger logger = LoggerFactory.getLogger(HelloWorld.class);
   * </pre>
   *
   * One can use (and copy!):
   *
   * <pre>
   *     Logger logger = LoggerFactory.getLogger(classType());
   * </pre>
   *
   * @return The Class object of the caller.
   * @see #className()
   */
  public static Class<?> classType() {
    try {
      return Class.forName(new Exception().getStackTrace()[1].getClassName());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the name of the class in which it is called. So, to create a java logger, instead of:
   *
   * <pre>
   *     Logger logger = Logger.getLogger(MyClass.class.getName());
   * </pre>
   *
   * One can use (and copy!):
   *
   * <pre>
   *     Logger logger = Logger.getLogger(className());
   * </pre>
   *
   * @return The Class object of the caller.
   * @see #getClass()
   */
  public static String className() {
    var classFullName = new Exception().getStackTrace()[1].getClassName().split("\\.");
    return classFullName[classFullName.length - 1];
  }

  /**
   * Run a command during the build and replace the caller with the output.
   *
   * <p>Add warning if there are any standard output from the command.
   *
   * <p>Error or timeout (30 seconds hardcoded) will stop the build.
   *
   * <p>For example, to print the build git hash code, one can use:
   *
   * <pre>
   * import static me.ykaplan.jmacros.LiteralMacro.*;
   *
   * public class Test {
   *
   *   public static void main(String... args) {
   *     System.out.println("Build hash code - " + executeScript("git rev-parse --short HEAD"));
   *   }
   * }
   * </pre>
   *
   * @param command The command to run (by the compiler)
   * @return The output of the command.
   */
  public static String executeScript(String command) {
    try {
      var commands = command.split("\\s+");
      var process = Runtime.getRuntime().exec(commands);
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

  /**
   * Replace the method invocation with an array of bytes which has the content of the file (as it
   * was available to the compiler).
   *
   * <p>For example, to print a logo as BASE 64:
   *
   * <pre>
   * import static me.ykaplan.jmacros.LiteralMacro.*;
   *
   * import java.util.Base64;
   *
   * public class Test {
   *
   *   public static void main(String... args) {
   *     var icon = fileContentAsBytes("./logo.png");
   *     var base64 = Base64.getEncoder().encodeToString(icon);
   *     System.out.println("&lt;img src='data:image/jpeg;base64, " + base64 + "'/&gt;");
   *   }
   * }
   * </pre>
   *
   * @param filePath The path to the file.
   * @return The content of the file.
   * @see #fileContentAsString(String)
   */
  public static byte[] fileContentAsBytes(String filePath) {
    try {
      var path = Paths.get(filePath);
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Replace the method invocation with the content of the file as a String (as it was available to
   * the compiler).
   *
   * <p>For example:
   *
   * <pre>
   *     var readme = fileContentAsString("./readme.txt");
   * </pre>
   *
   * @param filePath The path to the file.
   * @return The content of the file as a string.
   * @see #fileContentAsBytes(String)
   */
  public static String fileContentAsString(String filePath) {
    try {
      var path = Paths.get(filePath);
      return Files.readString(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the name of the file (equivalent to __FILE__ in C) without creating an exception.
   *
   * <p>For example:
   *
   * <pre>
   *     System.out.println(fileName() +":" + lineNumber() +" Hi");
   * </pre>
   *
   * @return The name of the source file.
   * @see #lineNumber()
   * @see #methodName()
   * @see #classType()
   * @see #className()
   */
  public static String fileName() {
    return new Exception().getStackTrace()[1].getFileName();
  }

  /**
   * Run the script argument as a JavaScript during compilation and replace the invocation with the
   * value.
   *
   * <p>If the output is Number, Boolean, String or Null it will be used as literal. For example:
   *
   * <pre>
   *  var seven = javaScript("4 + 3");
   * </pre>
   *
   * If the output is a JavaScript Hash or array,, it's JSON will be used as literal. For example:
   *
   * <pre>
   *  var array = javaScript("var e = [1,2,3,{four: 4}, null, false]; e.push('12'); e");
   * </pre>
   *
   * Any other return object will cause a compilation error.
   *
   * <p>Available only in JDK 11, 13 and 14, after that Nashorn <a
   * href="https://openjdk.java.net/jeps/372">was removed</a> from the JVM.
   *
   * @param script The script to run.
   * @return The output of the script.
   */
  @SuppressWarnings("removal")
  public static Object javaScript(String script) {
    try {
      System.setProperty("nashorn.args", "--no-deprecation-warning");
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
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the name of the line number (equivalent to __LINE__ in C) without creating an exception.
   *
   * <p>For example:
   *
   * <pre>
   *     System.out.println(fileName() +":" + lineNumber() +" Hi");
   * </pre>
   *
   * @return The number of the line.
   * @see #fileName()
   * @see #methodName()
   * @see #classType()
   * @see #className()
   */
  public static int lineNumber() {
    return new Exception().getStackTrace()[1].getLineNumber();
  }

  /**
   * Return the name of the calling method (if called within a method) without creating an
   * exception.
   *
   * <p>For example:
   *
   * <pre>
   *     System.out.println(fileName() +":" + lineNumber() +"#" + methodName() +" Hi");
   * </pre>
   *
   * @return The name of the method.
   * @see #fileName()
   * @see #lineNumber()
   * @see #classType()
   * @see #className()
   */
  public static String methodName() {
    return new Exception().getStackTrace()[1].getMethodName();
  }

  /**
   * Replace the method invocation with the content of the URL (as array of bytes). Create exception
   * if the URL is not available.
   *
   * <p>For example:
   *
   * @param urlAsString The URL.
   * @return The content of the URL as array of bytes.
   * @see #urlContentAsString(String)
   */
  public static byte[] urlContentAsBytes(String urlAsString) {
    try {
      var url = new URL(urlAsString);
      try (var reader = url.openStream(); ) {
        return reader.readAllBytes();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Replace the method invocation with the content of the URL (as String). Create exception if the
   * URL is not available.
   *
   * <p>For example:
   *
   * @param urlAsString The URL.
   * @return The content of the URL as String.
   * @see #urlContentAsString(String)
   */
  public static String urlContentAsString(String urlAsString) {
    return new String(urlContentAsBytes(urlAsString));
  }
}
