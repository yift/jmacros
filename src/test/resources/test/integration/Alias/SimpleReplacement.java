import me.ykaplan.jmacros.Alias;

import java.util.Date;

@Alias("Sql([A-Z][a-zA-Z0-9]*)  as  java.sql.\\1 ")
public class SimpleReplacement {


    public static String go() {
        var date1 = new Date();
        var date2 = new SqlDate(date1.getTime());
        return (date1.getTime() == date2.getTime())?"OK":"Ooops";
    }
}
