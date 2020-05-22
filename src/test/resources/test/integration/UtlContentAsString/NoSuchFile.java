import me.ykaplan.jmacros.macros.*;

public class NoSuchFile {


    public static String go() {
        return "length is: " + UrlContentAsString("no such file!").length;

    }
}