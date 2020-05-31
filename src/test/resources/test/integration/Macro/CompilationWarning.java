import me.ykaplan.jmacros.LiteralMacro;

public class CompilationWarning {


    public static String go() {
        return doFail("-");
    }

    private static LiteralMacro doFail(String arg) {
        java.util.Map myMap = new java.util.HashMap();
        myMap.put(arg, "-");
        return myMap.toString();
    }

}