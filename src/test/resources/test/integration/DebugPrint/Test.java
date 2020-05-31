import me.ykaplan.jmacros.DebugPrint;
import java.util.Optional;

public class Test {


    @DebugPrint
    public static String go() {


        var q = System.currentTimeMillis();
        var p = java.util.Optional.of(12);
        var r = Optional.empty();

        return "Hello world";
    }
}
