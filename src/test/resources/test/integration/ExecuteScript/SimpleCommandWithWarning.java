import me.ykaplan.jmacros.LiteralMacro;

public class SimpleCommandWithWarning {


    public static String go() {

        return "output is: " + LiteralMacro.executeScript("src/test/resources/test/integration/ExecuteScript/test2.sh").trim();

    }
}