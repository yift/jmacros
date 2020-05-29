import me.ykaplan.jmacros.macros.LiteralMacro;

public class ReturnNumber {

    private static int getB() {
        return 1;
    }

    public static String go() {
        int a = AsFactory.myNullMacro();
        int b = ReturnNumber.getB();
        String c;
        if(ReturnNumber.myBooleanMacro(a,0)) {
            c = "Yes";
        } else {
            c="No";
        }

        return new AsFactory().myNullMacro(1) + myIntMacro(a, b, a+1) + " " + c +" " + myBooleanMacro(a, a) +" " + myNullMacro(a) + " " + myNullMacro("b") + " " + myNullMacro(c);
    }

    private static LiteralMacro myIntMacro(String... args) {
        int ret = 0;
        for(String arg: args) {
            ret += arg.length();
        }
        return ret;
    }

    private static LiteralMacro myBooleanMacro(String arg1, String arg2) {
        return arg1.equals(arg2);
    }

    private static LiteralMacro myNullMacro(String arg) {
        if(arg.contains("a")) {
            return null;
        }
        if(arg.contains("b")) {
            return "nop";
        }
        return myIntMacro(arg);
    }
}

class AsFactory {
    public static int myNullMacro() {
        return 0;
    }

    public String myNullMacro(Object a) {
        return "hello ";
    }

}