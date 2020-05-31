import static me.ykaplan.jmacros.LiteralMacro.javaScript;

public class BadReturn {


    public static String go() {

        return "invalid! " + javaScript("var myObject = Java.type('java.util.HashMap'); new myObject()");

    }
}