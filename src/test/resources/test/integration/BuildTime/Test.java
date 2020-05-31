import static me.ykaplan.jmacros.LiteralMacro.buildTime;

import java.time.Instant;

public class Test {


    public static String go() {
        var now = Instant.now();
        if(((buildTime().isBefore(now)) || (now.equals(buildTime()))) &&
                (buildTime().isAfter(Instant.ofEpochMilli(1590170892790L)))) {
            return "Yep";
        }
        return "Nop";
    }
}
