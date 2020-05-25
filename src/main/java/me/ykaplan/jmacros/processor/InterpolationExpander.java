package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import me.ykaplan.jmacros.Interpolation;

public class InterpolationExpander implements UnitProcessable {
  private static final ExpressionReplacer replacer = new ExpressionReplacer();

  @Override
  public void processUnit(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree) {
    compilationUnitTree.forEachOfType(JCTree.JCAnnotation.class, this::processAnnotation);
  }

  private void processAnnotation(TreeElement<JCTree.JCAnnotation> annotation) {
    if (!annotation.getElement().type.toString().equals(Interpolation.class.getCanonicalName())) {
      // Not interpolation, no need to continue
      return;
    }

    var attributes =
        annotation.getElement().attribute.getElementValues().entrySet().stream()
            .filter(Objects::nonNull)
            .collect(
                Collectors.toMap(
                    attribute -> attribute.getKey().getSimpleName().toString(),
                    attribute -> {
                      var value = attribute.getValue().getValue();
                      if (!(value instanceof String)) {
                        annotation.error(
                            attribute.getKey().getSimpleName().toString() + " must be a String");
                        return (String) null;
                      }
                      return attribute.getValue().getValue().toString();
                    }));
    String startsWith = attributes.getOrDefault("startsWith", "`");
    String endsWith = attributes.getOrDefault("endsWith", startsWith);
    if ((startsWith == null) || (startsWith.isEmpty())) {
      annotation.error("startsWith can not be empty");
      return;
    }
    if ((endsWith == null) || (endsWith.isEmpty())) {
      annotation.error("endsWith can not be empty");
      return;
    }
    var block = annotation.getParent().getParent();
    removeMe(annotation);
    block.forEachOfType(
        JCTree.JCLiteral.class, literal -> processLiteral(literal, startsWith, endsWith));
  }

  private void removeMe(TreeElement<JCTree.JCAnnotation> annotation) {
    var parent = (JCTree.JCModifiers) (annotation.getParent().getElement());
    parent.annotations = List.filter(parent.annotations, annotation.getElement());
  }

  private void processLiteral(
      TreeElement<JCTree.JCLiteral> literal, String startsWith, String endsWith) {
    if (!(literal.getElement().value instanceof String)) {
      return;
    }
    var value = literal.getElement().value.toString();
    int searchFrom = 0;
    var builder = literal.getBuilder();
    var toReplace = new ArrayList<JCTree.JCExpression>();
    while (true) {
      int startAt = value.indexOf(startsWith, searchFrom);
      if (startAt == -1) {
        if (toReplace.isEmpty()) {
          return;
        }
        var text = value.substring(searchFrom);
        toReplace.add(builder.createLiteral(text));
        break;
      }
      int upUntil = value.indexOf(endsWith, startAt + startsWith.length());
      if (upUntil == -1) {
        literal.error("Could not find end of interpolation.");
        return;
      }
      toReplace.add(builder.createLiteral(value.substring(searchFrom, startAt)));
      var interpolationText = value.substring(startAt + startsWith.length(), upUntil);
      var interpolationParser = new SubTreeParser(interpolationText, literal);
      var interpolationExpression = interpolationParser.parse();
      if (interpolationExpression == null) {
        return;
      }
      toReplace.add(interpolationExpression);
      searchFrom = upUntil + endsWith.length();
    }
    JCTree.JCExpression expression = builder.createAdd(toReplace.get(0), toReplace.get(1));
    for (int i = 2; i < toReplace.size(); ++i) {
      expression = builder.createAdd(expression, toReplace.get(i));
    }
    if (!replacer.replace(literal, expression)) {
      literal.error("Could not use String interpolation");
    }
  }
}
