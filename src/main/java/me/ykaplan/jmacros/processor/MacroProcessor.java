package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("*")
public class MacroProcessor extends AbstractProcessor {
  private JavacProcessingEnvironment javaProcessingEnvironment;
  private static final Collection<UnitProcessable> processors =
      List.of(
          new MacroExtractor(),
          new InterpolationExpander(),
          new IdentifierReplacer(),
          new DebugPrinter(),
          new AliasApplier());

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    if (!(processingEnvironment instanceof JavacProcessingEnvironment)) {
      processingEnvironment
          .getMessager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              "Can not cast processingEnvironment to JavacProcessingEnvironment!");
    } else {
      javaProcessingEnvironment = (JavacProcessingEnvironment) processingEnvironment;
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    roundEnv.getRootElements().stream()
        .map(this::getUnit)
        .filter(Objects::nonNull)
        .forEach(this::processUnit);

    return false;
  }

  private void processUnit(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
    processors.forEach(p -> p.processUnit(compilationUnitTree));
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private TreeElement<JCCompilationUnit> getUnit(Element element) {
    return TreeElement.getUnit(element, javaProcessingEnvironment);
  }
}
