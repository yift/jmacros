import me.ykaplan.jmacros.LiteralMacro;

public class Test {


    public static String go() {

    return "Class name is " + LiteralMacro.className() + " -- " + AnotherClass.getName();
    }
}

class AnotherClass {
    private final static String NAME = LiteralMacro.className();
    public static String getName() {
        return LiteralMacro.className() + "  " + NAME +" " + new innerClass().myName();
    }

    private static class innerClass {
        public String myName() {
            return LiteralMacro.className();
        }
    }

}