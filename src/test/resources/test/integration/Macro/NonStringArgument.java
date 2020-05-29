import me.ykaplan.jmacros.macros.LiteralMacro;

public class NonStringArgument {


    public static String go() {
        return macroOne(1) + " " + macroTwo(false);
    }

    private static LiteralMacro macroOne(int i) {
        return i + 1;
    }
    private static LiteralMacro macroTwo(Boolean b) {
        return b;
    }
}