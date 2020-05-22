import me.ykaplan.jmacros.macros.MethodName;

import java.util.function.Function;

public class Test {
    private final static String noMethod = MethodName;

    public static String go() {
        return "Methods are " + MethodName +" -- " + noMethod + " " + getOtherMethodName() + " - " + withInLambda();
    }

    private static String getOtherMethodName() {
        return MethodName;
    }
    private static String withInLambda() {
        Function<String, String> a  = str -> MethodName + " " + str;
        return a.apply(MethodName);
    }
}