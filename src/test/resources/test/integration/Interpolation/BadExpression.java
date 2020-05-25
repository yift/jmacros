import me.ykaplan.jmacros.Interpolation;

public class BadExpression {
    @Interpolation
    public static String go() {
        return "`{ What the ...?`";
    }
}