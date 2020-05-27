import me.ykaplan.jmacros.macros.*;

public class Test {


    public static String go() {
        return "Class name is " + ClassType.getCanonicalName() + " -- " + AnotherClass.getName();
    }
}

class AnotherClass {
    private final static Class<?> CLASS = ClassType;
    public static String getName() {
        return ClassType.getSimpleName() + "  " + CLASS +" " + new innerClass().myName();
    }

    private static class innerClass {
        public String myName() {
            return ClassType.getCanonicalName();
        }
    }

}