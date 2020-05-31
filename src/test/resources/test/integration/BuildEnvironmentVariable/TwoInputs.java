import static me.ykaplan.jmacros.LiteralMacro.*;

public class TwoInputs {


    public static String go() {

        return "variable is " + buildEnvironmentVariable("a", "b");

    }
}