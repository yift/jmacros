import static me.ykaplan.jmacros.LiteralMacro.*;

public class KnownInput {


    public static String go() {

        return "variable is |" + (buildEnvironmentVariable("PATH").length() > 0) + "|";

    }
}