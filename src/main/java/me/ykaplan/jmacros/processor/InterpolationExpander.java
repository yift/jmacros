package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import me.ykaplan.jmacros.Interpolation;

public class InterpolationExpander extends AnnotationProcessable<String> {

  protected InterpolationExpander() {
    super(Interpolation.class);
  }

  @Override
  protected Optional<String> validateAttribute(
      String name, Object value, TreeElement<JCTree.JCAnnotation> annotation) {
    if (!(value instanceof String)) {
      annotation.error(name + " must be a String");
      return Optional.empty();
    }
    var str = value.toString();
    if (str.isEmpty()) {
      annotation.error(name + " can not be empty");
      return Optional.empty();
    }
    return Optional.of(str);
  }

  @Override
  protected void process(
      TreeElement<? extends JCTree> parent,
      TreeElement<JCTree.JCAnnotation> annotation,
      Map<String, String> attributes) {
    var startsWith = attributes.getOrDefault("startsWith", "`");
    var endsWith = attributes.getOrDefault("endsWith", startsWith);
    if ((endsWith == null) || (endsWith.isEmpty())) {
      annotation.error("endsWith can not be empty");
      return;
    }
    parent.forEachOfType(
        JCTree.JCLiteral.class, literal -> processLiteral(literal, startsWith, endsWith));
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
    if (!ExpressionReplacer.replace(literal, expression)) {
      literal.error("Could not use String interpolation");
    }
  }
}
