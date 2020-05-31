import static me.ykaplan.jmacros.LiteralMacro.fileContentAsBytes;

public class IntegerInput {


    public static String go() {

        return "content is " + fileContentAsBytes(100);

    }
}