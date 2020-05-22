import me.ykaplan.jmacros.macros.LineNumber;

public class LineNumberAsArrayIndex {
    public static String go() {
        char[] array =
                {'1', 'a', 'b', 'c', 'd', 'e', 'f', '-', '5'};
        return "Char is " + array[LineNumber];
    }
}