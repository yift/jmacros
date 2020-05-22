import me.ykaplan.jmacros.macros.*;

public class ReadFile {


    public static String go() {
        return "length is: " + FileContentAsBytes("src/test/resources/test/integration/test.txt").length + " - 3rd byte is " + FileContentAsBytes("src/test/resources/test/integration/test.txt")[3];

    }
}