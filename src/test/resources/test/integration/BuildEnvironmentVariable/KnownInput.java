import me.ykaplan.jmacros.macros.*;

public class KnownInput {


    public static String go() {

        return "variable is |" + (BuildEnvironmentVariable("PATH").length() > 0) + "|";

    }
}