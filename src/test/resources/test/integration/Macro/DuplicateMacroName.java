import me.ykaplan.jmacros.LiteralMacro;

public class DuplicateMacroName {


    public static String go() {

        return macro(1) + " " + macro(1, 2);
    }

    private static LiteralMacro macro(String a) {
        return "";
    }
    private static LiteralMacro macro(String a, String b) {
        return "a";
    }
}