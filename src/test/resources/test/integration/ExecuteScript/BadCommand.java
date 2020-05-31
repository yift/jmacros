import static me.ykaplan.jmacros.LiteralMacro.executeScript;

public class BadCommand {


    public static String go() {

        return "variable is " + executeScript("noScuchCommand");

    }
}