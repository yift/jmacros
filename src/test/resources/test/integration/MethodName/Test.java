import static me.ykaplan.jmacros.LiteralMacro.methodName;

import java.util.function.Function;

public class Test {
    private final static String noMethod = methodName();

    public static String go() {
        return "Methods are " + methodName() +" -- " + noMethod + " " + getOtherMethodName() + " - " + withInLambda();
    }

    private static String getOtherMethodName() {
        return methodName();
    }
    private static String withInLambda() {
        Function<String, String> a  = str -> methodName() + " " + str;
        return a.apply(methodName());
    }
}