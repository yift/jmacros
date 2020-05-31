import static me.ykaplan.jmacros.LiteralMacro.*;

public class SimpleCommand {


    public static String go() {

        return "output is: " + executeScript("src/test/resources/test/integration/ExecuteScript/test1.sh").trim();

    }
}