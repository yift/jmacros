import me.ykaplan.jmacros.Alias;

import java.time.Instant;

@Alias({"SqlDate  as  java sql Date "})
public class InvalidReplacement {


    public static String go() {
        return "OK";
    }
}
