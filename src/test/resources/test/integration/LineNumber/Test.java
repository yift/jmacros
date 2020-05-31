import static me.ykaplan.jmacros.LiteralMacro.lineNumber;

public class Test {


    public static String go() {

        return "LineNumber is " + lineNumber() +


                " and now " +


                Math.max(0L, lineNumber());

    }
}