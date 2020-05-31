import me.ykaplan.jmacros.LiteralMacro;

public class NoSuchFile {


    public static String go() {
        return "length is: " + LiteralMacro.fileContentAsString("no such file!").length;

    }
}