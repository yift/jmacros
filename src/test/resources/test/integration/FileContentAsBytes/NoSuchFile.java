import static me.ykaplan.jmacros.LiteralMacro.fileContentAsBytes;

public class NoSuchFile {


    public static String go() {
        return "length is: " + fileContentAsBytes("no such file!").length;

    }
}