package me.ykaplan.jmacros.test.integration;

import com.moandjiezana.toml.Toml;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import javax.tools.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

public class IntegrationTests {
  private static final Toml toml = new Toml();
  private static final boolean nashornSupported;

  static {
    boolean supported = false;
    try {
      Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
      supported = true;
    } catch (ClassNotFoundException e) {
      supported = false;
    }
    nashornSupported = supported;
  }

  @Test
  public void integrationTests() throws Exception {
    workOnAllFiles();
  }

  private void workOnAllFiles() throws Exception {
    var classLoader = getClass().getClassLoader();
    var path = Paths.get(classLoader.getResource("test/integration/").toURI());
    Files.walk(path)
        .filter(Files::isRegularFile)
        .filter(p -> p.toString().endsWith(".toml"))
        .forEach(this::workOnFile);
  }

  private void runTest(Path configFile) throws Exception {
    var exe = new Execution(configFile);
    exe.go();
  }

  private void workOnFile(Path configFile) {
    try {
      runTest(configFile);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class Execution {
    private final Path javaSourceFile;
    private final Toml config;
    private final String testName;
    private final JavaCompiler compiler;
    private final FileManager fileManager;
    private boolean compiled = false;
    private Object executionOutput;
    private final List<Message> warnings = new ArrayList<>();
    private final List<Message> errors = new ArrayList<>();

    private Execution(Path tomlFile) {
      config = toml.read(tomlFile.toFile());
      var dir = tomlFile.getParent();
      testName = tomlFile.getFileName().toString().replace(".toml", "");
      System.out.println("Testing " + dir.getFileName() + ":" + testName);
      javaSourceFile = dir.resolve(testName + ".java");
      compiler = ToolProvider.getSystemJavaCompiler();
      fileManager = new FileManager(compiler.getStandardFileManager(null, null, null));
    }

    private void go() throws Exception {
      if (config.getBoolean("needNashorn", false)) {
        if (!nashornSupported) {
          System.out.println("Nashorn is not supported in this JVM");
          return;
        }
      }
      compile();
      if (compiled) {
        execute();
      }
      verify();
    }

    private void verify() {
      var softly = new SoftAssertions();
      var shouldCompile = config.getBoolean("shouldCompile", true);
      softly.assertThat(shouldCompile).isEqualTo(compiled);
      var expectedOutput = config.getString("output", null);
      softly.assertThat(expectedOutput).isEqualTo(executionOutput);

      var expectedErrors =
          Optional.ofNullable(config.getTables("expectedErrors")).orElse(List.of());
      expectedErrors = removeUnwantedMessages(expectedErrors);
      softly.assertThat(errors).hasSameSizeAs(expectedErrors);
      if (errors.size() == expectedErrors.size()) {
        for (int i = 0; i < errors.size(); ++i) {
          errors.get(i).verify(softly, expectedErrors.get(i));
        }
      }

      var expectedWarnings =
          Optional.ofNullable(config.getTables("expectedWarnings")).orElse(List.of());
      expectedWarnings = removeUnwantedMessages(expectedWarnings);
      softly.assertThat(warnings).hasSameSizeAs(expectedWarnings);
      if (warnings.size() == expectedWarnings.size()) {
        for (int i = 0; i < warnings.size(); ++i) {
          warnings.get(i).verify(softly, expectedWarnings.get(i));
        }
      }

      softly.assertAll();
    }

    private List<Toml> removeUnwantedMessages(List<Toml> messages) {
      var javaVersion = System.getProperty("java.version").split("\\.")[0];
      return messages.stream()
          .filter(m -> m.getString("onlyFor", javaVersion).equals(javaVersion))
          .collect(Collectors.toList());
    }

    private void execute() throws Exception {
      var classLoader =
          new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) {
              var bytes = fileManager.files.get(name).bytes.toByteArray();
              return defineClass(name, bytes, 0, bytes.length);
            }
          };
      var clazz = classLoader.loadClass(testName);
      var method = clazz.getMethod("go");
      executionOutput = method.invoke(null);
    }

    private void compile() {
      var output = new StringWriter();
      var args = List.of("-classpath", System.getProperty("java.class.path"), "-Xlint:deprecation");
      var units = List.of(new FileObject(javaSourceFile));
      var task =
          compiler.getTask(
              output,
              fileManager,
              diagnostic -> {
                if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                  errors.add(new Message(diagnostic));
                } else {
                  warnings.add(new Message(diagnostic));
                }
              },
              args,
              null,
              units);
      compiled = task.call();
    }
  }

  private static class Message {
    private final String message;
    private final long lineNumber;
    private final long columnNumber;

    private Message(Diagnostic<? extends JavaFileObject> message) {
      this.message = message.getMessage(null);
      this.lineNumber = message.getLineNumber();
      this.columnNumber = message.getColumnNumber();
    }

    public void verify(SoftAssertions softly, Toml expected) {
      softly
          .assertThat(message)
          .isEqualTo(
              expected.getString("text", "").replace("$PWD", System.getProperty("user.dir")));
      softly.assertThat(lineNumber).isEqualTo(expected.getLong("line", -1L));
      softly.assertThat(columnNumber).isEqualTo(expected.getLong("column", -1L));
    }

    @Override
    public String toString() {
      return "{text= \"" + message + "\", line=" + lineNumber + ", column= " + columnNumber + "}";
    }
  }

  private static class FileObject extends SimpleJavaFileObject {
    private final Path file;

    protected FileObject(Path javaFile) {
      super(javaFile.toUri(), Kind.SOURCE);
      file = javaFile;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
      return Files.readString(file).replace("__HOME__", System.getProperty("user.dir"));
    }
  }

  private static class ClassFile extends SimpleJavaFileObject {
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    protected ClassFile(String className) {
      super(URI.create("string://" + className), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() {
      return bytes;
    }
  }

  private static class FileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final Map<String, ClassFile> files = new HashMap<>();

    protected FileManager(StandardJavaFileManager fileManager) {
      super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
        Location location,
        String className,
        JavaFileObject.Kind kind,
        javax.tools.FileObject sibling) {
      return files.computeIfAbsent(className, ClassFile::new);
    }
  }
}
