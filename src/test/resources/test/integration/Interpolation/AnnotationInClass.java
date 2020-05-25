import me.ykaplan.jmacros.Interpolation;

@Interpolation
public class AnnotationInClass {
    public static String go() {
        return retOne() + retTwo() + retThree() + retFour();
    }

    private static String retOne() {
        return "`0.1`";
    }
    private static String retTwo() {
        return " - `100L`";
    }
    private static String retThree() {
        return "`\" hello\"` world";
    }
    private static String retFour() {
        return "!";
    }
}