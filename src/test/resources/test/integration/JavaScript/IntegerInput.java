import static me.ykaplan.jmacros.LiteralMacro.javaScript;

public class IntegerInput {


    public static String go() {

        return "variable is " + javaScript(100);

    }
}