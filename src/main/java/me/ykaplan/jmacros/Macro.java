package me.ykaplan.jmacros;

/**
 * This class can be used to create code in compile time.
 *
 * <p>To use, declare a static private unique method that returns a Macro and accept only String
 * arguments. The implementation should return a String that is the new expression that the compiler
 * will replace the any call for the method, and each of the String argument will be the actual
 * expression.
 *
 * <p>An exception will be translate into a compilation error.
 *
 * <p>For example, to print an expression and it's value, one can use:
 *
 * <pre>
 * import java.util.*;
 * import me.ykaplan.jmacros.Macro;
 *
 * public class Test {
 *
 *   public static void main(String... args) throws Exception {
 *     int a = 0;
 *     var map = Map.of(1, "one", 2, "two", 3, "three", 0, "zero");
 *     debug("start", a, map, map.get(a));
 *     a++;
 *     debug("now", a, map, map.get(a), "one");
 *   }
 *
 *   private static Macro debug(String prefix, String... expressions) {
 *     var ret = new StringBuilder("System.out.println(");
 *     ret.append(prefix).append(" + \":\"");
 *     for (var expression : expressions) {
 *       ret.append(" + \"\\n\\t")
 *           .append(expression.replace("\"", "\\\""))
 *           .append(" = \" + (")
 *           .append(expression)
 *           .append(")");
 *     }
 *     ret.append(")");
 *
 *     return ret.toString();
 *   }
 * }
 * </pre>
 *
 * @see LiteralMacro
 */
public abstract class Macro {}
