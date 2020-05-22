import me.ykaplan.jmacros.macros.FileName;
import me.ykaplan.jmacros.macros.LineNumber;

public class Test {


    public static String go() {

        return "File is Test.java? " + FileName.endsWith("Test.java");

    }
}