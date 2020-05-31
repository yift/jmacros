import me.ykaplan.jmacros.LiteralMacro;

public class ExceptionInMacro {


    public static String go() {
        int i = 0;
        return myMacro(1) + myMacro(1, 2);
    }

    private static LiteralMacro myMacro(String... args) throws Exception {
        if(args.length > 1) {
            throw new Exception("Too many arguments");
        }
        if(args.length < 1) {
            throw new Exception("Too few arguments");
        }
        return args[0];
    }
}