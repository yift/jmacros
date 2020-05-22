import me.ykaplan.jmacros.macros.*;

import java.time.Instant;

public class Test {


    public static String go() {
        var now = Instant.now();
        if(((BuildTime.isBefore(now)) || (now.equals(BuildTime))) &&
                (BuildTime.isAfter(Instant.ofEpochMilli(1590170892790L)))) {
            return "Yep";
        }
        return "Nop";
    }
}
