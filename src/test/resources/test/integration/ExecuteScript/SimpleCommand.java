import me.ykaplan.jmacros.macros.*;

public class SimpleCommand {


    public static String go() {

        return "output is: " + ExecuteScript("src/test/resources/test/integration/ExecuteScript/test1.sh").trim();

    }
}