import me.ykaplan.jmacros.macros.*;

public class SimpleCommandWithWarning {


    public static String go() {

        return "output is: " + ExecuteScript("src/test/resources/test/integration/ExecuteScript/test2.sh").trim();

    }
}