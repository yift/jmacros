import me.ykaplan.jmacros.Macro;
import me.ykaplan.jmacros.macros.LiteralMacro;

public class BadReturn {


    public static String go() {
        return macroOne() + macroTwo();
    }

    private static LiteralMacro macroOne() {
        return new Object();
    }
    private static Macro macroTwo() {
        return null;
    }
}