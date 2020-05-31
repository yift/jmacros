import static me.ykaplan.jmacros.LiteralMacro.javaScript;

public class SimpleScripts {

    private String retString() {
        return javaScript("'hello'")+ " world";
    }

    private String retNumber() {
        return "" + (javaScript("1 + 5") + 1);
    }

    private String retBooleam() {
        return "" + javaScript("1 === 1");
    }

    private String retNull() {
        return "" + javaScript("null");
    }

    private String retJson() {
        return javaScript("[1, 2, 3, {a: 10}, null]");
    }

    public static String go() {

        var test = new SimpleScripts();
        return test.retJson() + "\n" + test.retNull() +"\n" + test.retNumber() +"\n" + test.retString() +"\n" + test.retBooleam();

    }
}