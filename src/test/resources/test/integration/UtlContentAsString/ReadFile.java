import me.ykaplan.jmacros.macros.*;

public class ReadFile {


    public static String go() {
        return "content is: " +
                UrlContentAsString("file://__HOME__/src/test/resources/test/integration/test.txt");
    }
}