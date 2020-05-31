
public class ReadFile {


    public static String go() {
        return "length is: " +
                me.ykaplan.jmacros.LiteralMacro.urlContentAsBytes("file://__HOME__/src/test/resources/test/integration/test.txt").length +
                " - 3rd byte is " +
                me.ykaplan.jmacros.LiteralMacro.urlContentAsBytes("file://__HOME__/src/test/resources/test/integration/test.txt")[3];
    }
}