import static me.ykaplan.jmacros.LiteralMacro.javaScript;

public class BadScript {


    public static String go() {

        return "invalid! " + javaScript("{");

    }
}