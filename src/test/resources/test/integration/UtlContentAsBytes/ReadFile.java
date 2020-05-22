import me.ykaplan.jmacros.macros.*;

public class ReadFile {


    public static String go() {
        return "length is: " +
                UrlContentAsBytes("file://__HOME__/src/test/resources/test/integration/test.txt").length +
                " - 3rd byte is " +
                UrlContentAsBytes("file://__HOME__/src/test/resources/test/integration/test.txt")[3];
    }
}