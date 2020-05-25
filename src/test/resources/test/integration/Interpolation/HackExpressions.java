import me.ykaplan.jmacros.Interpolation;

public class HackExpressions {
    @Interpolation
    public static String go() {
        var a = "`1;}} class B { public int doIt() {return 3`";
        var b = "`1;} public int doIt() {return 3`";
        var c = "`1; int d =0`";
        return "";
    }
}