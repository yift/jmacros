import static me.ykaplan.jmacros.LiteralMacro.fileContentAsBytes;

public class ReadFile {


    public static String go() {
        return "length is: " + fileContentAsBytes("src/test/resources/test/integration/test.txt").length + " - 3rd byte is " + fileContentAsBytes("src/test/resources/test/integration/test.txt")[3];

    }
}