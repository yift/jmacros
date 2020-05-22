import me.ykaplan.jmacros.macros.LineNumber;

public class LineNumberWithCast {
    public static String go() {
        char[] array =
                {'1', 'a', 'b', 'c', 'd', 'e', 'f', '-', '5'};
        return "Char is " + array[(int)LineNumber];
    }
}