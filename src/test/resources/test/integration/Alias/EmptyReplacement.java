import me.ykaplan.jmacros.Alias;

import java.time.Instant;

@Alias({"SqlDate  as   "})
public class EmptyReplacement {


    public static String go() {
        return "OK";
    }
}
