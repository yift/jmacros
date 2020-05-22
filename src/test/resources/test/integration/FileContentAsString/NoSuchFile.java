import me.ykaplan.jmacros.macros.*;

public class NoSuchFile {


    public static String go() {
        return "length is: " + FileContentAsString("no such file!").length;

    }
}