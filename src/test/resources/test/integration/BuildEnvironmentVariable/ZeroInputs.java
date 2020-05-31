import static me.ykaplan.jmacros.LiteralMacro.*;

public class ZeroInputs {


    public static String go() {

        return "variable is " + buildEnvironmentVariable();

    }
}