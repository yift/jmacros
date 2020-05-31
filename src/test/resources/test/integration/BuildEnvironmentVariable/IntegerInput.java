import static me.ykaplan.jmacros.LiteralMacro.*;

public class IntegerInput {


    public static String go() {

        return "variable is " + buildEnvironmentVariable(100);

    }
}