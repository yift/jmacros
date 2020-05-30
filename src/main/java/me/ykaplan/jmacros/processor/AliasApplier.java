package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import me.ykaplan.jmacros.Alias;

class AliasApplier extends AnnotationProcessable<Function<String, String>> {
  private static final Pattern replaceNumbers = Pattern.compile("\\\\[\\d]+");
  private static final Pattern validJavaIdentifier = Pattern.compile("^[A-Za-z_$][A-Za-z_$0-9.]*$");

  protected AliasApplier() {
    super(Alias.class);
  }

  @Override
  protected Optional<Function<String, String>> validateAttribute(
      String name, Object value, TreeElement<JCTree.JCAnnotation> element) {
    if (!(value instanceof List)) {
      element.error("Value must be a list");
      return Optional.empty();
    }
    var list = (List<?>) value;
    if (list.isEmpty()) {
      element.error("Value can not be empty");
      return Optional.empty();
    }
    Function<String, String> ret = UnaryOperator.identity();
    for (Object item : list) {
      if (item instanceof Attribute) {
        item = ((Attribute) item).getValue();
      }

      if (!(item instanceof String)) {
        element.error("Value must be a string");
        return Optional.empty();
      }
      var str = item.toString();
      var index = str.indexOf(" as ");
      if (index == -1) {
        element.error("Value must have format of: '<regexp> as <name>'");
        return Optional.empty();
      }
      var regex = str.substring(0, index);
      if (regex.isBlank()) {
        element.error("regular expression can not be empty");
        return Optional.empty();
      }

      var replacement = str.substring(index + 4).trim();
      if (replacement.isBlank()) {
        element.error("Replacement can not be empty");
        return Optional.empty();
      }

      var numbers = replaceNumbers.matcher(replacement);
      var toValidate = numbers.replaceAll("Aaa");
      if (!validJavaIdentifier.matcher(toValidate).matches()) {
        element.error("Replacement: '" + replacement + "' must be java identifier");
        return Optional.empty();
      }

      try {
        var pattern = Pattern.compile("^" + regex.trim() + "$");
        Function<String, String> replaceMe =
            identifier -> {
              var match = pattern.matcher(identifier);
              if (!match.matches()) {
                return identifier;
              }

              var newText = replacement;
              for (int i = 1; i <= match.groupCount(); ++i) {
                newText = newText.replace("\\" + i, match.group(i));
              }
              return newText;
            };
        ret = ret.andThen(replaceMe);
      } catch (Exception e) {
        element.error("Invalid regular expression: " + regex);
        return Optional.empty();
      }
    }
    return Optional.of(ret);
  }

  @Override
  protected void process(
      TreeElement<? extends JCTree> parent,
      TreeElement<JCTree.JCAnnotation> annotation,
      Map<String, Function<String, String>> attributes) {
    var value = attributes.get("value");
    if (value == null) {
      return;
    }

    var builder = parent.getBuilder();
    parent.forEachOfType(
        JCTree.JCIdent.class,
        ident -> {
          var oldName = ident.getElement().name.toString();
          var newName = value.apply(oldName);
          if (!oldName.equals(newName)) {
            var toReplace = builder.createIdent(newName);
            ExpressionReplacer.replace(ident, toReplace);
          }
        });
  }
}
