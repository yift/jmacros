import static me.ykaplan.jmacros.LiteralMacro.executeScript;

public class IntegerInput {


    public static String go() {

        return "variable is " + executeScript(100);

    }
}