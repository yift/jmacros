import me.ykaplan.jmacros.macros.LiteralMacro;

public class NotPrivateOrStatic {


    public static String go() {
        return macroOne() + " " + macroTwo();
    }

    static LiteralMacro macroOne() {
        return " ";
    }
    private LiteralMacro macroTwo() {
        return " ";
    }
}