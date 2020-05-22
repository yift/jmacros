import me.ykaplan.jmacros.macros.*;

public class FailedCommandWithWarning {


    public static String go() {

        return "output is: " + ExecuteScript("src/test/resources/test/integration/ExecuteScript/test3.sh").trim();

    }
}