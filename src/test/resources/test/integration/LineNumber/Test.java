import me.ykaplan.jmacros.macros.LineNumber;

public class Test {


    public static String go() {

        return "LineNumber is " + LineNumber +


                " and now " +


                Math.max(0L, LineNumber);

    }
}