package me.ykaplan.jmacros;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation provide a way to define an "alias" within a specific context. An alias, is
 * another way to call an identifier. Either because there are two identifiers with the same name
 * (for example {@link java.util.Date} and {@link java.sql.Date}), because one of the names is
 * "wrong" in the specific context, because the name is too long and can be shorten...
 *
 * <p>To use, one must define a list of aliases, each one in the format: {code toReplace as
 * replacement} where:
 *
 * <ul>
 *   <li>{@code toReplace} Is a valid regular expression of all the identifier to replace with in
 *       the code.
 *   <li>{@code replacement} Is the name to replace. If the toReplace had any regular expression
 *       groups, the replacement can use a {@code \\n} for the replacement.
 * </ul>
 *
 * <p>For example, to use both Date classes:
 *
 * <pre>
 * import me.ykaplan.jmacros.Alias;
 *
 * {@literal @}Alias({"SqlDate as java.sql.Date", "UtilDate as java.util.Date"})
 * public class Test {
 *
 *   public static void main(String... args) {
 *     var utilDate = new UtilDate();
 *     var sqlDate = new SqlDate(utilDate.getTime());
 *     System.out.println("utilDate = " + utilDate + "; sqlDate = " + sqlDate);
 *   }
 * }
 * </pre>
 *
 * @see #value()
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
public @interface Alias {
  /**
   * List of replacement values. Each one should be in the format {code toReplace as replacement}
   * where:
   *
   * <ul>
   *   <li>{@code toReplace} Is a valid regular expression of all the identifier to replace with in
   *       the code.
   *   <li>{@code replacement} Is the name to replace. If the toReplace had any regular expression
   *       groups, the replacement can use a {@code \\n} for the replacement.
   * </ul>
   *
   * For example with regular expression:
   *
   * <pre>
   * import me.ykaplan.jmacros.Alias;
   *
   * {@literal @}Alias("Util([A-Z][A-Za-z0-9]*) as java.util.\\1")
   * public class Test {
   *
   *   public static void main(String... args) {
   *     var list = UtilList.of(1, 2, 3, 4);
   *     System.out.println(list);
   *   }
   * }
   * </pre>
   *
   * @return replacements values. See above.
   */
  String[] value();
}
