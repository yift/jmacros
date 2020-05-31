import static me.ykaplan.jmacros.LiteralMacro.fileName;

public class Test {


    public static String go() {

        return "File is Test.java? " + fileName().endsWith("Test.java");

    }
}