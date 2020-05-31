import static me.ykaplan.jmacros.LiteralMacro.fileContentAsString;

public class ReadFile {


    public static String go() {
        return "content is: " + fileContentAsString("src/test/resources/test/integration/test.txt");

    }
}