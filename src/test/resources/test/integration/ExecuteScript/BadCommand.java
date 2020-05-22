import me.ykaplan.jmacros.macros.*;

public class BadCommand {


    public static String go() {

        return "variable is " + ExecuteScript("noScuchCommand");

    }
}