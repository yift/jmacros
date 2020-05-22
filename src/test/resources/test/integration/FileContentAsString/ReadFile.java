import me.ykaplan.jmacros.macros.*;

public class ReadFile {


    public static String go() {
        return "content is: " + FileContentAsString("src/test/resources/test/integration/test.txt");

    }
}