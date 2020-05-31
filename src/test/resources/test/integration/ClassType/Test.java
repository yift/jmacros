import static me.ykaplan.jmacros.LiteralMacro.*;

public class Test {


    public static String go() {
        return "Class name is " + classType().getCanonicalName() + " -- " + AnotherClass.getName();
    }
}

class AnotherClass {
    private final static Class<?> CLASS = classType();
    public static String getName() {
        return classType().getSimpleName() + "  " + CLASS +" " + new innerClass().myName();
    }

    private static class innerClass {
        public String myName() {
            return classType().getCanonicalName();
        }
    }

}