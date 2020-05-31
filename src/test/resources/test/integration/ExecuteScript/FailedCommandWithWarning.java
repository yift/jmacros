import static me.ykaplan.jmacros.LiteralMacro.executeScript;

public class FailedCommandWithWarning {


    public static String go() {

        return "output is: " + executeScript("src/test/resources/test/integration/ExecuteScript/test3.sh").trim();

    }
}