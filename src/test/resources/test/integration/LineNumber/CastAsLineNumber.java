import me.ykaplan.jmacros.macros.LineNumber;

public class CastAsLineNumber {
    public static String go() {
        int i = (LineNumber) 12 + 4;
        return " " + i;
    }
}