import me.ykaplan.jmacros.macros.*;

public class TwoInputs {


    public static String go() {

        return "variable is " + BuildEnvironmentVariable("a", "b");

    }
}