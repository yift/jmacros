import me.ykaplan.jmacros.macros.*;

public class UnknownInput {


    public static String go() {

        return "variable is |" + BuildEnvironmentVariable("__UnknownInput__") + "|";

    }
}