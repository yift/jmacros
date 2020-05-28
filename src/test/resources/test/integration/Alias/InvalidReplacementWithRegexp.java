import me.ykaplan.jmacros.Alias;

import java.time.Instant;

@Alias({"Sql([A-Z][A-Za-z]*)  as  java sql \\1 "})
public class InvalidReplacementWithRegexp {


    public static String go() {
        return "OK";
    }
}
