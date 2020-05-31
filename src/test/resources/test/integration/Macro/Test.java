import me.ykaplan.jmacros.LiteralMacro;

public class Test {


    public static String go() {
        int i = 0;
        return myMacro(i+1, i+2, i+3, "a", "b");
    }

    private static LiteralMacro myMacro(String e1, String e2, String... args) {
        String ret = "e1 = " + e1;
        ret = ret +" e2 = " + e2;
        for(int i = 0;i<args.length; ++i) {
            ret = ret +" arg[" + i +"] = " + args[i];
        }
        return ret;
    }
}