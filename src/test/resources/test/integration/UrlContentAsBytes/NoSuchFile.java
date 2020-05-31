import me.ykaplan.jmacros.LiteralMacro.*;

public class NoSuchFile {


    public static String go() {
        return "length is: " + urlContentAsBytes("no such file!").length;

    }
}