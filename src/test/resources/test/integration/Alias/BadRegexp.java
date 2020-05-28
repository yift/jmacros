import me.ykaplan.jmacros.Alias;

import java.time.Instant;

@Alias({"[-- as world"})
public class BadRegexp {


    public static String go() {
        return "OK";
    }
}
