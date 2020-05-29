package me.ykaplan.jmacros.processor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.*;

abstract class InternalCompiler {
  private final String code;
  protected final String className;
  private final Map<String, ClassFile> files = new HashMap<>();

  public InternalCompiler(String code, String className) {
    this.code = code;
    this.className = className;
  }

  public abstract void gotError(String error);

  public abstract void gotWarning(String warning);

  void parsed(CompilationUnitTree unit) {}

  public byte[] getRawClass(String className) {
    return files.get(className).bytes.toByteArray();
  }

  boolean compile() {
    var output = new StringWriter();
    List<String> args = List.of();
    var units = List.of(new FileObject());
    var compiler = ToolProvider.getSystemJavaCompiler();
    var task =
        (JavacTask)
            compiler.getTask(
                output,
                new FileManager(compiler.getStandardFileManager(null, null, null)),
                diagnostic -> {
                  if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                    gotError(diagnostic.getMessage(null));
                  } else {
                    gotWarning(diagnostic.getMessage(null));
                  }
                },
                args,
                null,
                units);
    task.addTaskListener(
        new TaskListener() {
          @Override
          public void finished(TaskEvent e) {
            if (e.getKind() == TaskEvent.Kind.PARSE) {
              parsed(e.getCompilationUnit());
            }
          }
        });
    return task.call();
  }

  private class FileObject extends SimpleJavaFileObject {
    protected FileObject() {
      super(URI.create("file:/" + className + ".java"), Kind.SOURCE);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }

  private class FileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
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

  private class ClassFile extends SimpleJavaFileObject {
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    protected ClassFile(String className) {
      super(URI.create("string://" + className), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() {
      return bytes;
    }
  }
}
