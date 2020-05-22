import me.ykaplan.jmacros.macros.*;

public class SimpleScripts {

    private String retString() {
        return JavaScript("'hello'")+ " world";
    }

    private String retNumber() {
        return "" + (JavaScript("1 + 5") + 1);
    }

    private String retBooleam() {
        return "" + JavaScript("1 === 1");
    }

    private String retNull() {
        return "" + JavaScript("null");
    }

    private String retJson() {
        return JavaScript("[1, 2, 3, {a: 10}, null]");
    }

    public static String go() {

        var test = new SimpleScripts();
        return test.retJson() + "\n" + test.retNull() +"\n" + test.retNumber() +"\n" + test.retString() +"\n" + test.retBooleam();

    }
}