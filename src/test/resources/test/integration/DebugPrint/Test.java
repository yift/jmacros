import me.ykaplan.jmacros.DebugPrint;

public class Test {


    public static String go() {

        @DebugPrint
        var e = Test.class;

        return "Hello world";
    }
}
