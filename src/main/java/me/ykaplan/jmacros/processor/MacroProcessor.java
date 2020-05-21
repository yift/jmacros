package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
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

  private void processUnit(TreeElement<JCCompilationUnit> compilationUnitTree) {
    var imports = new MacrosImportsHandler(compilationUnitTree);
    if (!imports.anyMacroSupporter()) {
      // Nothing to do
      return;
    }
    compilationUnitTree.getElement().defs = imports.newDefs();
    var macroHandlerFactory = new MacroHandlerFactory(imports);
    compilationUnitTree.forEachOfType(
        JCTree.JCIdent.class, ident -> processIdentifier(ident, macroHandlerFactory));
  }

  private void processIdentifier(
      TreeElement<JCTree.JCIdent> ident, MacroHandlerFactory macroHandlerFactory) {
    var handler = macroHandlerFactory.createHandler(ident);
    handler.ifPresent(h -> h.replace());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private TreeElement<JCCompilationUnit> getUnit(Element element) {
    return TreeElement.getUnit(element, javaProcessingEnvironment);
  }
}
