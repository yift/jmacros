import static me.ykaplan.jmacros.LiteralMacro.*;

public class UnknownInput {


    public static String go() {

        return "variable is |" + buildEnvironmentVariable("__UnknownInput__") + "|";

    }
}