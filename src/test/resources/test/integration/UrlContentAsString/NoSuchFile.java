import me.ykaplan.jmacros.LiteralMacro;

public class NoSuchFile {


    public static String go() {
        return "length is: " + LiteralMacro.urlContentAsString("no such file!").length;

    }
}