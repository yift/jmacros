import me.ykaplan.jmacros.Interpolation;
import java.util.*;

public class DifferentStartAndStops {
    private static Map<String, Integer> theMap = new LinkedHashMap<>();
    public static String go() {
        theMap.put("This", 1);
        theMap.put("is", 2);
        return retOne() + retTwo() + retThree() + retFour();
    }

    @Interpolation(startsWith = "{", endsWith = "}")
    private static String retOne() {
        return "the map is {theMap}";
    }

    @Interpolation(startsWith = "\\")
    private static String retTwo() {
        return " with size \\theMap.size()\\";
    }

    @Interpolation(startsWith = "$(", endsWith = ")$")
    private static String retThree() {
        return " theMap[This] = $(theMap.get(\"This\"))$";
    }

    private static String retFour()
    {
        return "  - `theMap`";
    }
}