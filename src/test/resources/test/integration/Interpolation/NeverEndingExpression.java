import me.ykaplan.jmacros.Interpolation;

public class NeverEndingExpression {
    @Interpolation
    public static String go() {
        return "This will start - `hashCode()";
    }
}