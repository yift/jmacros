import static me.ykaplan.jmacros.LiteralMacro.urlContentAsString;

public class ReadFile {


    public static String go() {
        return "content is: " +
                urlContentAsString("file://__HOME__/src/test/resources/test/integration/test.txt");
    }
}