package me.ykaplan.jmacros.processor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.tree.JCTree;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.tools.*;

class SubTreeParser {
  private final String code;
  private final TreeElement<?> element;

  public SubTreeParser(String code, TreeElement<?> element) {
    this.code = "class Test { public Object go() {return " + code + ";}}";
    this.element = element;
  }

  JCTree.JCExpression parse() {
    return compile();
  }

  private JCTree.JCExpression findExpression(CompilationUnitTree compiledTree) {
    var tree = (JCTree.JCCompilationUnit) compiledTree;
    if ((tree.defs.size() != 1) || (!(tree.defs.get(0) instanceof JCTree.JCClassDecl))) {
      element.error("Invalid expression");
      return null;
    }
    var classDecl = (JCTree.JCClassDecl) tree.defs.get(0);
    if ((classDecl.defs.size() != 1) || (!(classDecl.defs.get(0) instanceof JCTree.JCMethodDecl))) {
      element.error("Invalid expression");
      return null;
    }
    JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) classDecl.defs.get(0);
    if ((method.body.stats.size() != 1)
        || (!(method.body.stats.get(0) instanceof JCTree.JCReturn))) {
      element.error("Invalid expression");
      return null;
    }
    var retStatement = (JCTree.JCReturn) method.body.stats.get(0);
    var expression = retStatement.getExpression();
    TreeMover.move(expression, element);

    return expression;
  }

  private JCTree.JCExpression compile() {
    var output = new StringWriter();
    List<String> args = List.of();
    var units = List.of(new FileObject());
    var compiler = ToolProvider.getSystemJavaCompiler();
    AtomicReference<JCTree.JCExpression> expression = new AtomicReference<>();
    var task =
        (JavacTask)
            compiler.getTask(
                output,
                new FileManager(compiler.getStandardFileManager(null, null, null)),
                diagnostic -> {
                  if (expression.get() == null) {
                    if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                      element.error(diagnostic.getMessage(null));
                    } else {
                      element.warning(diagnostic.getMessage(null));
                    }
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
              var r1 = findExpression(e.getCompilationUnit());
              expression.set(r1);
            }
          }
        });
    task.call();
    return expression.get();
  }

  private class FileObject extends SimpleJavaFileObject {
    protected FileObject() {
      super(URI.create("file:/Test.java"), Kind.SOURCE);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }

  private class FileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
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
