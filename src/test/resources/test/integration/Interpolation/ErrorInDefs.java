import me.ykaplan.jmacros.Interpolation;

public class ErrorInDefs {
    @Interpolation(startsWith = "")
    public static String go() {
        return goAgain();
    }
    @Interpolation(endsWith = "")
    public static String goAgain() {
        return "Hello";
    }
}