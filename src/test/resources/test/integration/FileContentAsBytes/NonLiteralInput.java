import me.ykaplan.jmacros.macros.*;

public class NonLiteralInput {


    public static String go() {

        return "" + FileContentAsBytes(System.getenv("hi"));

    }
}