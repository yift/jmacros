import me.ykaplan.jmacros.macros.*;

public class BadScript {


    public static String go() {

        return "invalid! " + JavaScript("{");

    }
}