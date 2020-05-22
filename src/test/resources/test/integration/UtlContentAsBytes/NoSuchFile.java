import me.ykaplan.jmacros.macros.*;

public class NoSuchFile {


    public static String go() {
        return "length is: " + UrlContentAsBytes("no such file!").length;

    }
}