import me.ykaplan.jmacros.macros.LiteralMacro;

public class CompilationError {


    public static String go() {

        int a = 200;
        int b = 1;
        int c = 10;
        int d = -30;
        return " -- " + min(a,b,c) + "; " + min(a) + "; " + min(a,b,c,d,a,b,c + 4,d);
    }

    private static LiteralMacro doFail(String arg1, String... args) {
        return new NoSuchObject();
    }

}