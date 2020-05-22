import me.ykaplan.jmacros.macros.*;

public class BadReturn {


    public static String go() {

        return "invalid! " + JavaScript("var myObject = Java.type('java.util.HashMap'); new myObject()");

    }
}