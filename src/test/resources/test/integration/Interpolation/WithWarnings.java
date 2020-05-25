import me.ykaplan.jmacros.Interpolation;
import java.util.*;

public class WithWarnings {
    @Interpolation
    public static String go() {
        var a = new ArrayList();

        return "`(List<String>) a`";
    }
}