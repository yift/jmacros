import me.ykaplan.jmacros.macros.*;

public class Test {


    public static String go() {

        return "Class name is " + ClassName + " -- " + AnotherClass.getName();
    }
}

class AnotherClass {
    private final static String NAME = ClassName;
    public static String getName() {
        return ClassName + "  " + NAME +" " + new innerClass().myName();
    }

    private static class innerClass {
        public String myName() {
            return ClassName;
        }
    }

}