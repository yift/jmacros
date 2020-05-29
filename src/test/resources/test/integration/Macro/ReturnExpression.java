import me.ykaplan.jmacros.Macro;
import me.ykaplan.jmacros.Expression;

public class ReturnExpression {


    public static String go() {
        int a = 200;
        int b = 1;
        int c = 10;
        int d = -30;
        return " -- " + min(a,b,c) + "; " + min(a) + "; " + min(a,b,c,d,a,b,c + 4,d);
    }

    private static Macro min(String arg1, String... args) {
        var minSoFar = arg1;
        for(var arg : args) {
            minSoFar = "(" + minSoFar +") < (" + arg +") ? " + minSoFar +" : " + arg;
        }
        return minSoFar;
    }

}