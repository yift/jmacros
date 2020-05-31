import static me.ykaplan.jmacros.LiteralMacro.fileContentAsBytes;

public class NonLiteralInput {


    public static String go() {

        return "" + fileContentAsBytes(System.getenv("hi"));

    }
}