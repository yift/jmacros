package me.ykaplan.jmacros;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allow <a href="https://en.wikipedia.org/wiki/String_interpolation">String Interpolation</a>. Can
 * be define within a class/enum.interface or a specific method. Once defined, all the literal
 * string within the method/type will be replaced.
 *
 * <p>For example:
 *
 * <pre>
 * import me.ykaplan.jmacros.Interpolation;
 *
 * {@literal @}Interpolation
 * public class Test {
 *
 *   public static void main(String... args) {
 *     var apples = 4;
 *     var bananas = 3;
 *     System.out.println("I have `apples` apples and `bananas` bananas, making `apples + bananas` pieces of fruit in total.");
 *   }
 * }
 * </pre>
 *
 * Please note: As it is still a string, quotes must be escaped within the expression.
 *
 * @see #startsWith()
 * @see #endsWith()
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
public @interface Interpolation {
  /**
   * Identify where an expression starts. Please note, expression should not hold this. For example:
   *
   * <pre>
   * import me.ykaplan.jmacros.Interpolation;
   *
   * public class Test {
   *
   *   {@literal @}Interpolation(startsWith="$(", endsWith=")")
   *   public static void main(String... args) {
   *     var apples = 4;
   *     var bananas = 3;
   *     System.out.println("I have $(apples) apples and $(bananas) bananas, making $(apples + bananas) pieces of fruit in total.");
   *   }
   * }
   * </pre>
   *
   * @return Where should an expression start. default to : `
   * @see #endsWith()
   */
  String startsWith() default "`";

  /**
   * Identify where an expression ends. Please note, expression should not hold this.
   *
   * @return Where should an expression ends. default to : startWith
   * @see #startsWith()
   */
  String endsWith() default "`";
}
