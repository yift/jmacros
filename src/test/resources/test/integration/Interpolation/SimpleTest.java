import me.ykaplan.jmacros.Interpolation;

public class SimpleTest {
    @Interpolation
    public static String go() {
        int a = 10;
        return "a + `30` = `a + 30`";
    }
}